package x3.player.mru.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.AppInstance;
import x3.player.mru.common.NetUtil;
import x3.player.mru.config.AmfConfig;
import x3.player.mru.rmqif.module.RmqClient;
import x3.player.mru.rmqif.module.RmqServer;
import x3.player.mru.room.RoomInfo;
import x3.player.mru.room.RoomManager;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionManager;
import x3.player.mru.simulator.UdpRelayManager;
import x3.player.mru.surfif.module.SurfChannelBuilder;
import x3.player.mru.surfif.module.SurfChannelManager;
import x3.player.mru.surfif.module.SurfConnectionManager;

import javax.xml.ws.Service;

public class ServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);

    private static final boolean USE_PING = false;

    private static ServiceManager serviceManager = null;

    public static ServiceManager getInstance() {
        if (serviceManager == null) {
            serviceManager = new ServiceManager();
        }

        return serviceManager;
    }

    private RmqServer rmqServer;
    private SessionManager sessionManager;
    private HeartbeatManager heartbeatManager;
    private SurfConnectionManager surfConnectionManager;

    private boolean isQuit = false;

    /**
     * Reads a config file in the constructor
     */
    public ServiceManager() {
        AppInstance instance = AppInstance.getInstance();
        instance.setConfig(new AmfConfig(instance.getInstanceId(), instance.getConfigFile()));
    }

    /**
     * Main loop
     */
    public void loop() {
        AmfConfig config = AppInstance.getInstance().getConfig();

        if (USE_PING && !pingRmqServer(config.getRmqHost())) {
            return;
        }

        startService();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            logger.warn("Process is about to quit (Ctrl+C)");
            isQuit = true;

            stopService();
            }));

        while (!isQuit) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Process End");
    }

    /**
     * Returns a ping result to a rabbitmq server
     * @param host
     * @return
     */
    private boolean pingRmqServer(String host) {
        logger.info("Checking RMQ target [{}]", host);
        boolean rmqAvailable = NetUtil.ping(host, 1000);
        logger.info("Host [{}] is {}", host, rmqAvailable ? "reachable" : "NOT reachable");

        return rmqAvailable;
    }

    /**
     * Initializes pre-process
     * @return
     */
    private boolean startService() {
        rmqServer = new RmqServer();
        rmqServer.start();

        sessionManager = SessionManager.getInstance();
        sessionManager.start();

        heartbeatManager = HeartbeatManager.getInstance();
        heartbeatManager.start();

        surfConnectionManager = SurfConnectionManager.getInstance();
        surfConnectionManager.start();

        return true;
    }

    /**
     * Finalizes all the resources
     */
    private void stopService() {

        if (surfConnectionManager != null) {
            surfConnectionManager.stop();
        }

        if (rmqServer != null) {
            rmqServer.stop();
        }

        heartbeatManager.stop();
        sessionManager.stop();

        AmfConfig config = AppInstance.getInstance().getConfig();

        if (RmqClient.hasInstance(config.getMcudName())) {
            RmqClient.getInstance(config.getMcudName()).closeSender();
        }
    }

    public boolean releaseResource(String sessionId) {
        if (sessionId == null) {
            return false;
        }

        if (sessionManager == null) {
            return false;
        }

        SessionInfo sessionInfo = sessionManager.getSession(sessionId);

        if (sessionInfo == null) {
            logger.warn("[{}] No session found", sessionId);
            return false;
        }

        int groupId = -1;
        int mixerId = -1;
        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
        if (roomInfo == null) {
            logger.error("[{}] No roomInfo found", sessionInfo.getSessionId());
        }
        else {
            groupId = roomInfo.getGroupId();
            mixerId = roomInfo.getMixerId();
        }

        // Closes local udp resources
        UdpRelayManager udpRelayManager = UdpRelayManager.getInstance();
        udpRelayManager.close(sessionId);

        // Closes Surf resources
        if (sessionInfo.isCaller()) {
            int cgRxId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_CG_RX);
            int cgTxId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_CG_TX);
            int parId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_PAR_CG);

            removeSurfResource(sessionId, groupId, cgRxId);
            removeSurfResource(sessionId, groupId, cgTxId);
            removeSurfResource(sessionId, groupId, parId);
        }
        else {
            int calleeId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_CD);

            removeSurfResource(sessionId, groupId, calleeId);
        }

        // Closes room info
        if (sessionInfo.getConferenceId() != null) {
            RoomManager.getInstance().removeSession(sessionInfo.getConferenceId(), sessionId);

            // Closes the mixer
            if (roomInfo.getSessionSize() == 0) {
                removeSurfResource(sessionId, groupId, mixerId);
                removeSurfResource(sessionId, groupId,
                        SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_BG));
                removeSurfResource(sessionId, groupId,
                        SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_MENT));
                removeSurfResource(sessionId, groupId,
                        SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_PAR_BG));
                removeSurfResource(sessionId, groupId,
                        SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_PAR_MENT));
            }


        }

        sessionManager.deleteSession(sessionId);

        return true;
    }

    private void removeSurfResource(String sessionId, int groupId, int toolId) {
        SurfChannelBuilder builder = new SurfChannelBuilder(toolId);
        builder.setRemove();

        String json = builder.build();

        SurfConnectionManager.getInstance().addSendQueue(sessionId, groupId, toolId, json);
    }
}
