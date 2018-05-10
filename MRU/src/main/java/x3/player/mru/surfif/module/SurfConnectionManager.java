package x3.player.mru.surfif.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.core.socket.TcpSocket;
import x3.player.mru.AppInstance;
import x3.player.mru.common.NetUtil;
import x3.player.mru.common.StringUtil;
import x3.player.mru.config.AmfConfig;
import x3.player.mru.session.SessionStateManager;
import x3.player.mru.session.SessionStateMessage;
import x3.player.mru.surfif.handler.SurfProcCommand;
import x3.player.mru.surfif.handler.SurfProcConnect;
import x3.player.mru.surfif.handler.SurfProcSetConfig;
import x3.player.mru.surfif.types.SurfConstant;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SurfConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(SurfConnectionManager.class);

    private static final int REQUEST_TABLE_SIZE =  64;
    private static final int MAX_REQUEST_ID = 8192;
    private static final int QUEUE_SIZE = 64;

    private static SurfConnectionManager connectionManager = null;

    public static SurfConnectionManager getInstance() {
        if (connectionManager == null) {
            connectionManager = new SurfConnectionManager();
        }

        return connectionManager;
    }

    private TcpSocket socket = null;
    private SurfConnectionThread connectionThread = null;
    private boolean isSurfConnected = false;
    private int reqId;
    private Map<Integer, Object> requestTable = null;

    private BlockingQueue<SurfSendQueueInfo> sendQueue;
    private Thread sendQueueThread;

    public SurfConnectionManager() {
        requestTable = new HashMap<>(REQUEST_TABLE_SIZE);
        reqId = 0;

        sendQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
        sendQueueThread = new Thread(new SendQueuePuller(sendQueue));
        sendQueueThread.start();
    }

    public void start() {
        AmfConfig config = AppInstance.getInstance().getConfig();
        boolean result = connect(config.getSurfIp(), config.getSurfPort());

        logger.debug("Surf connection .... [{}]", StringUtil.getOkFail(result));
    }

    public void stop() {
        disconnect();

        sendQueueThread.interrupt();
        sendQueueThread = null;
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

        sendInitMessage();
        sendConnectMessage();
        sendMonitorMessage();

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

    public boolean addSendQueue(String sessionId, int groupId, int toolId, String msg) {
        if (!isSurfConnected) {
            logger.error("{} Surf not connected", sessionId);
            return false;
        }

        SurfSendQueueInfo info = new SurfSendQueueInfo();

        info.setSessionId(sessionId);
        info.setGroupId(groupId);
        info.setToolId(toolId);
        info.setMsg(msg);

        sendQueue.add(info);

        return true;
    }

    private boolean sendInitMessage() {
        if (socket == null) {
            return false;
        }

        int result = socket.send(SurfConstant.STR_INIT_MESSAGE.getBytes());

        logger.debug("-> Surf send: result {}", result);

        return (result > 0) ? true : false;
    }

    private boolean sendConnectMessage() {
        SurfProcConnect proc = new SurfProcConnect();
        String jsonStr = proc.build();

        return (send(jsonStr) > 0) ? true : false;
    }

    private boolean sendMonitorMessage() {
        SurfProcSetConfig sysReq = new SurfProcSetConfig();
        String json = sysReq.build(true);
//        String json = sysReq.build(false);

        return (send(json) > 0) ? true : false;
    }

    private boolean sendClearAll() {
        SurfProcCommand cmd = new SurfProcCommand();
        String json = cmd.buildClearAll();

        return (send(json) > 0) ? true : false;
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
            }

            @Override
            public void onReady() {
                logger.debug("Surf onReady");
                sendClearAll();

                isSurfConnected = true;
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

    class SendQueuePuller implements Runnable {

        private BlockingQueue<SurfSendQueueInfo> queue;
        private boolean isQuit = false;

        public SendQueuePuller(BlockingQueue<SurfSendQueueInfo> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            logger.info("SendQueuePuller started");

            while(!isQuit) {
                try {
                    SurfSendQueueInfo info = queue.take();
                    handleQueueMessage(info);
                } catch (Exception e) {
                    logger.warn("Exception [{}] [{}]", e.getClass(), e.getMessage());
                    if (e.getClass() == InterruptedException.class || e.getClass() == SocketException.class) {
                        isQuit = true;
                    }
                }
            }

            logger.warn("SendQueuePuller end");
        }

        private void handleQueueMessage(SurfSendQueueInfo info) {
            if (info == null) {
                return;
            }

            logger.debug("[{}] -> Surf. groupId {} toolId {} msg {}", info.getSessionId(),
                    info.getGroupId(), info.getToolId(), info.getMsg());

            int result = send(info.getMsg());
            if (result <= 0) {
                //
                // TODO: Retry or what to do?
                //
            }
        }
    }
}
