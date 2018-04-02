package x3.player.mru.surfif.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.core.socket.TcpSocket;
import x3.player.mru.AppInstance;
import x3.player.mru.common.NetUtil;
import x3.player.mru.common.StringUtil;
import x3.player.mru.config.AmfConfig;
import x3.player.mru.surfif.types.SurfConstant;

public class SurfConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(SurfConnectionManager.class);

    private static SurfConnectionManager connectionManager = null;

    public static SurfConnectionManager getInstance() {
        if (connectionManager == null) {
            connectionManager = new SurfConnectionManager();
        }

        return connectionManager;
    }

    private TcpSocket socket = null;
    private SurfConnectionThread connectionThread = null;

    public SurfConnectionManager() {
    }

    public void start() {
        AmfConfig config = AppInstance.getInstance().getConfig();
        boolean result = connect(config.getSurfIp(), config.getSurfPort());

        logger.debug("Surf connection .... [{}]", StringUtil.getOkFail(result));
    }

    public void stop() {
        disconnect();
    }

    private boolean connect(String ip, int port) {
        if (socket != null) {
            disconnect();
        }

        socket = new TcpSocket();

        boolean result =socket.connect(ip, port);

        if (!result) {
            disconnect();
            return false;
        }

        createReadThread();

        return result;
    }

    private void disconnect() {

        if (connectionThread != null) {
            connectionThread.interrupt();
            connectionThread = null;
        }

        if (socket == null) {
            return;
        }

        socket.disconnect();
        socket = null;
    }

    private boolean sendInitMessage() {
        if (socket == null) {
            return false;
        }

        int result = socket.send(SurfConstant.STR_INIT_MESSAGE.getBytes());

        logger.debug("-> Surf send: result {}", result);

        return (result > 0) ? true : false;
    }

    public int send(String json) {
        if (json == null || socket == null) {
            return -1;
        }

        int jsonLength = json.length();
        byte[] bufLength = NetUtil.getLittleEndian4Bytes(jsonLength);

        int retHdr = socket.send(bufLength);

        if (retHdr <= 0) {
            return -1;
        }

        int retBody = socket.send(json.getBytes());

        logger.debug("-> Surf: msg={} result={}", json, retBody);

        if (retBody <= 0) {
            return -1;
        }

        return retHdr + retBody;
    }

    private void createReadThread() {
        connectionThread = new SurfConnectionThread(socket);
        connectionThread.setSurfConnectionCallback(new SurfConnectionCallback() {
            @Override
            public void onConnected() {
                logger.debug("Surf onConnected");
                sendInitMessage();
            }

            @Override
            public void onSend(String data) {
                logger.debug("Surf onSend");
                send(data);
            }
        });
        connectionThread.start();
    }
}
