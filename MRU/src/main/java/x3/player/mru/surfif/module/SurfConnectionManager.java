package x3.player.mru.surfif.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.core.socket.TcpSocket;
import x3.player.mru.AppInstance;
import x3.player.mru.common.NetUtil;
import x3.player.mru.common.StringUtil;
import x3.player.mru.config.AmfConfig;
import x3.player.mru.surfif.handler.SurfProcSysReq;
import x3.player.mru.surfif.types.SurfConstant;

import java.util.HashMap;
import java.util.Map;

public class SurfConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(SurfConnectionManager.class);

    private static final int REQUEST_TABLE_SIZE =  64;
    private static final int MAX_REQUEST_ID = 8192;

    private static SurfConnectionManager connectionManager = null;

    public static SurfConnectionManager getInstance() {
        if (connectionManager == null) {
            connectionManager = new SurfConnectionManager();
        }

        return connectionManager;
    }

    private TcpSocket socket = null;
    private SurfConnectionThread connectionThread = null;
    private int reqId;
    private Map<Integer, Object> requestTable = null;

    public SurfConnectionManager() {
        requestTable = new HashMap<>(REQUEST_TABLE_SIZE);
        reqId = 0;
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

    private boolean sendMonitorMessage() {
        SurfProcSysReq sysReq = new SurfProcSysReq();
        String json = sysReq.build();

        return (send(json) > 0) ? true : false;
    }

    public int send(String json) {
        if (json == null || socket == null) {
            return -1;
        }

        int jsonLength = json.length();
        byte[] bufLength = NetUtil.getLittleEndian4Bytes(jsonLength);

        logger.debug("Surf send length: {} {} {} {}", bufLength[0] & 0xff, bufLength[1] & 0xff, bufLength[2] & 0xff, bufLength[3] & 0xff);
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
            public void onReady() {
                logger.debug("Surf onReady");
                sendMonitorMessage();
            }

            @Override
            public void onSend(String data) {
                logger.debug("Surf onSend");
                send(data);
            }
        });
        connectionThread.start();
    }

    /**
     * Finds and returns new req_id
     * @return
     */
    public int newReqId() {

        int next;
        synchronized (requestTable) {
            next = reqId;
            reqId++;

            if (reqId >= MAX_REQUEST_ID) {
                reqId = 0;
            }
        }
        return next;
    }

    public boolean putRequestTable(int reqId, Object obj) {
        synchronized (requestTable) {
            requestTable.put(reqId, obj);
        }

        return true;
    }

    public Object getRequestTable(int reqId) {
        Object obj = null;

        synchronized (requestTable) {
            if (requestTable.containsKey(reqId)) {
                obj = requestTable.get(reqId);
            }
        }
        return obj;
    }

    public boolean removeRequestTable(int reqId) {
        boolean result = false;

        synchronized (requestTable) {
            if (requestTable.containsKey(reqId)) {
                requestTable.remove(reqId);
                result =true;
            }
        }

        return result;
    }
}
