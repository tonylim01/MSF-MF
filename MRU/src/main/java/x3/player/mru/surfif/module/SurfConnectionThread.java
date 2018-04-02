package x3.player.mru.surfif.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.core.socket.TcpSocket;
import x3.player.mru.common.JsonMessage;
import x3.player.mru.common.NetUtil;
import x3.player.mru.surfif.messages.SurfMsgConnect;
import x3.player.mru.surfif.types.SurfConstant;

import java.net.SocketException;

public class SurfConnectionThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(SurfConnectionThread.class);

    private static final int BUFFER_SIZE = 1024;

    private TcpSocket socket = null;
    private boolean isQuit = false;

    private SurfConnectionCallback surfConnectionCallback;

    public SurfConnectionThread(TcpSocket socket) {
        this.socket = socket;
    }

    public void setSurfConnectionCallback(SurfConnectionCallback callback) {
        surfConnectionCallback = callback;
    }

    @Override
    public void run() {

        logger.info("SurfConnectionThread start");

        String initMsg = readInitMessage();

        logger.debug("Read msg: {}", initMsg);
        if (initMsg != null && initMsg.equals(SurfConstant.STR_INIT_MESSAGE)) {
            if (surfConnectionCallback != null) {
                surfConnectionCallback.onConnected();
            }
            sendInitResponse();
        }

        logger.debug("SurfConnectionTread isQuit {}", isQuit);

        while (!isQuit) {
            if (socket == null) {
                break;
            }

            try {
                String body = readSurfMessage();

                //
                // TODO
                //
                if (body == null) {
                    continue;
                }

                logger.debug("Surf msg: {}", body);

            } catch (Exception e) {
                logger.warn("Exception [{}] [{}]", e.getClass(), e.getMessage());
                if (e.getClass() == InterruptedException.class || e.getClass() == SocketException.class) {
                    isQuit = true;
                }
            }
        }

        logger.info("SurfConnectionThread end");
    }

    private String readInitMessage() {
        if (socket == null) {
            return null;
        }

        byte[] buffer = new byte[BUFFER_SIZE];

        int result = socket.read(buffer);

        if (result <= 0) {
            return null;
        }

        logger.debug("<- Surf read bytes {}", result);

        return new String(buffer, 0, result);
    }

    private boolean sendInitResponse() {
        SurfMsgConnect msg = new SurfMsgConnect();

        msg.setVersion(1, 2);   // TODO
        msg.setKeeyAliveTime(0);    // TODO

        JsonMessage<SurfMsgConnect> jsonMessage = new JsonMessage<>(SurfMsgConnect.class);
        String jsonStr = jsonMessage.build(msg);

        logger.debug("Surf init json: {}", jsonStr);

        return (socket.send(jsonStr.getBytes()) > 0) ? true : false;
    }

    private String readSurfMessage() {
        if (socket == null) {
            return null;
        }

        byte[] header = new byte[4];

        int result;

        result = socket.read(header, header.length);
        if (result <= 0) {
            return null;
        }

        int bodySize = NetUtil.getBigEndian4BytesValue(header);
        logger.debug("<- Surf body size {}", bodySize);

        byte[] body = new byte[bodySize];
        result = socket.read(body, bodySize);

        if (result <= 0) {
            return null;
        }

        logger.debug("<- Surf read bytes {}", result);

        String bodyStr = new String(body);
        logger.debug("<- Surf body: {}", bodyStr);

        return bodyStr;
    }
}
