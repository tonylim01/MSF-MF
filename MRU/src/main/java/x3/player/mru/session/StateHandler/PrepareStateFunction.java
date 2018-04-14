package x3.player.mru.session.StateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.core.sdp.SdpInfo;
import x3.player.mru.AppInstance;
import x3.player.mru.config.AmfConfig;
import x3.player.mru.config.SurfConfig;
import x3.player.mru.room.RoomInfo;
import x3.player.mru.room.RoomManager;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionState;
import x3.player.mru.simulator.BiUdpRelayManager;
import x3.player.mru.surfif.module.SurfChannelManager;
import x3.player.mru.surfif.module.SurfConnectionManager;
import x3.player.mru.surfif.module.SurfVoiceBuilder;

public class PrepareStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(PrepareStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return;
        }

        logger.debug("{} PREPARE state", sessionInfo.getSessionId());

        if (sessionInfo.getServiceState() != SessionState.PREPARE) {
            sessionInfo.setServiceState(SessionState.PREPARE);
        }

        //
        // TODO
        // Callee's NegoDoneReq comes ahead of Caller's
        //
        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
        if (roomInfo == null) {
            logger.error("[{}] No roomInfo found", sessionInfo.getSessionId());
            return;
        }

        synchronized (roomInfo) {
            if (roomInfo.getMixerId() < 0) {
                // Step #1) Creates a voice mixer
                openMixerResource(roomInfo, sessionInfo.getSessionId());

                // Step #2) Create play & bg channels and connects them to the mixer
                openPlayResource(sessionInfo, roomInfo);
            }

            if (sessionInfo.isCaller()) {
                // Step #3) Creates 3 channels used by a caller
                openCallerResource(sessionInfo, roomInfo);
                // Step #4) Creates local relaying resource
                openRelayResource(sessionInfo);
            }
            else {
                // Step #5) Creates one channel for a callee
                openCalleeResource(sessionInfo, roomInfo);
            }
        }

    }

    /**
     * (RTP) -> (SURF caller) --> (Relay) --> (SURF par)
     *
     * @param sessionInfo
     * @return
     */
    private boolean openRelayResource(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return false;
        }

        logger.debug("[{}] Open relay resources", sessionInfo.getSessionId());

        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
        if (roomInfo == null) {
            logger.error("[{}] No roomInfo found", sessionInfo.getSessionId());
            return false;
        }

        SdpInfo sdpInfo = sessionInfo.getSdpInfo();
        if (sdpInfo == null) {
            return false;
        }

        SurfConfig surfConfig = AppInstance.getInstance().getConfig().getSurfConfig();

        BiUdpRelayManager udpRelayManager = BiUdpRelayManager.getInstance();

        // srcLocalPort -> Surf par
        int parPort = SurfChannelManager.getUdpPort(roomInfo.getGroupId(), SurfChannelManager.TOOL_ID_PAR_CG);

        logger.debug("[{}] Relay: remote (%s:%d) <- local (%d)", sessionInfo.getSessionId(),
                surfConfig.getSurfIp(), parPort, sessionInfo.getSrcLocalPort());

        udpRelayManager.openSrcServer(sessionInfo.getSessionId(), sessionInfo.getSrcLocalPort());
        udpRelayManager.openSrcClient(sessionInfo.getSessionId(),
                surfConfig.getSurfIp(), parPort);

        // TODO
        // srcLocalPort -> AIIS as well as -> Surf par
        //


        // dstLocalPort -> Surf caller
        int callerPort = SurfChannelManager.getUdpPort(roomInfo.getGroupId(), SurfChannelManager.TOOL_ID_CG_TX);

        logger.debug("[{}] Relay: remote (%s:%d) <- local (%d)", sessionInfo.getSessionId(),
                surfConfig.getSurfIp(), callerPort, sessionInfo.getDstLocalPort());

        udpRelayManager.openDstServer(sessionInfo.getSessionId(), sessionInfo.getDstLocalPort());
        udpRelayManager.openDstClient(sessionInfo.getSessionId(),
                surfConfig.getSurfIp(), callerPort);

        return true;
    }

    private boolean openMixerResource(RoomInfo roomInfo, String sessionId) {
        if (roomInfo == null) {
            return false;
        }

        SurfConnectionManager connectionManager = SurfConnectionManager.getInstance();
        SurfChannelManager channelManager = SurfChannelManager.getInstance();

        int groupId = roomInfo.getGroupId();

        if (groupId < 0) {
            return false;
        }

        logger.debug("({}) Allocates mixer on group [{}]", roomInfo.getRoomId(), groupId);

        // Creates a mixer
        int mixerId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_MIXER);
        roomInfo.setMixerId(mixerId);

        SurfVoiceBuilder builder = new SurfVoiceBuilder(mixerId);
        builder.setMixer(8000, 500, 5); // TODO : Temp. value
        String json = builder.build();

        connectionManager.addSendQueue(sessionId, groupId, mixerId, json);

        return true;
    }

    private boolean openCallerResource(SessionInfo sessionInfo, RoomInfo roomInfo) {
        if (sessionInfo == null) {
            return false;
        }

        logger.debug("[{}] Allocates caller DSP resources", sessionInfo.getSessionId());

        String json;
        int groupId = roomInfo.getGroupId();
        int mixerId = roomInfo.getMixerId();

        if (groupId < 0 || mixerId < 0) {
            return false;
        }

        AmfConfig config = AppInstance.getInstance().getConfig();

        SurfConnectionManager connectionManager = SurfConnectionManager.getInstance();
        SurfChannelManager channelManager = SurfChannelManager.getInstance();

        SdpInfo sdpInfo = sessionInfo.getSdpInfo();
        int localPayloadId = 8; // TODO : Internal packet's payloadId

        // Creates 3 voice channels
        int cgRxId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_CG_RX);
        int cgTxId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_CG_TX);
        int parId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_PAR_CG);

        int rxPort = SurfChannelManager.getUdpPort(cgRxId);
        //
        // TODO: Below is not working
        //
//        int txPort = ((rxPort << 16) & 0xffff0000) + SurfChannelManager.getUdpPort(cgTxId);
        int txPort = SurfChannelManager.getUdpPort(cgTxId);


        logger.debug("[{}] CG_RX ptp: remote (%s:%d) -> local (%d)", sessionInfo.getSessionId(),
                config.getLocalIpAddress(), sessionInfo.getSrcLocalPort(), rxPort);

        // Creates a caller as p2p mode (RX channel: remote -> local)
        SurfVoiceBuilder rxBuilder = new SurfVoiceBuilder(cgRxId);
        rxBuilder.setChannel(-1, true,
                sdpInfo.getPayloadId(), // inPayloadId
                localPayloadId,  // outpayloadId
                rxPort,
                config.getLocalIpAddress(), sessionInfo.getSrcLocalPort());
        json = rxBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, cgRxId, json);

        logger.debug("[{}] CG_TX ptp: remote (%s:%d) <- local (%d)", sessionInfo.getSessionId(),
                sdpInfo.getRemoteIp(), sdpInfo.getRemotePort(), txPort);

        // Creates a caller as p2p mode (TX channel: remote <- local)
        SurfVoiceBuilder txBuilder = new SurfVoiceBuilder(cgTxId);
        txBuilder.setChannel(-1, true,
                localPayloadId, // inPayloadId
                sdpInfo.getPayloadId(),  // outpayloadId
                txPort,
                sdpInfo.getRemoteIp(), sdpInfo.getRemotePort());
        json = txBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, cgTxId, json);

        int parPort = SurfChannelManager.getUdpPort(parId);

        logger.debug("[{}] CG_par fe: remote (%s:%d) - local (%d) - mixer (%d) ", sessionInfo.getSessionId(),
                config.getLocalIpAddress(), sessionInfo.getDstLocalPort(), parPort, mixerId);

        // Creates a mixer's participant as ip mode
        SurfVoiceBuilder parBuilder = new SurfVoiceBuilder(parId);
        parBuilder.setChannel(mixerId, true,
                localPayloadId,
                localPayloadId,
                parPort,
                config.getLocalIpAddress(), sessionInfo.getDstLocalPort());
        json = parBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, parId, json);

        return true;
    }

    private boolean openCalleeResource(SessionInfo sessionInfo, RoomInfo roomInfo) {
        if (sessionInfo == null) {
            return false;
        }

        logger.debug("{} Allocates callee DSP resources", sessionInfo.getSessionId());

        String json;
        int groupId = roomInfo.getGroupId();
        int mixerId = roomInfo.getMixerId();

        if (groupId < 0 || mixerId < 0) {
            return false;
        }

        SurfConnectionManager connectionManager = SurfConnectionManager.getInstance();
        SurfChannelManager channelManager = SurfChannelManager.getInstance();

        SdpInfo sdpInfo = sessionInfo.getSdpInfo();
        int localPayloadId = 8; // TODO : Internal packet's payloadId

        // Creates one voice channel
        int calleeId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_CD);
        int calleePort = SurfChannelManager.getUdpPort(calleeId);

        logger.debug("[{}] CD_par fe: remote (%s:%d) - local (%d) - mixer (%d) ", sessionInfo.getSessionId(),
                sdpInfo.getRemoteIp(), sdpInfo.getRemotePort(), calleePort, mixerId);

        // Creates a callee as ip mode
        SurfVoiceBuilder builder = new SurfVoiceBuilder(calleeId);
        builder.setChannel(mixerId, true,
                sdpInfo.getPayloadId(), // inPayloadId
                localPayloadId,  // outpayloadId
                calleePort,
                sdpInfo.getRemoteIp(), sdpInfo.getRemotePort());
        json = builder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, calleeId, json);

        return true;
    }

    private boolean openPlayResource(SessionInfo sessionInfo, RoomInfo roomInfo) {
        if (sessionInfo == null) {
            return false;
        }

        logger.debug("{} Allocates file play DSP resources", sessionInfo.getSessionId());

        String json;
        int groupId = roomInfo.getGroupId();
        int mixerId = roomInfo.getMixerId();

        if (groupId < 0 || mixerId < 0) {
            return false;
        }

        SurfConnectionManager connectionManager = SurfConnectionManager.getInstance();
        SurfChannelManager channelManager = SurfChannelManager.getInstance();

        SdpInfo sdpInfo = sessionInfo.getSdpInfo();
        int localPayloadId = 8; // TODO : Internal packet's payloadId

        // Creates bg & play channels
        int playId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_PAR_PLAY);
        int playPort =  SurfChannelManager.getUdpPort(playId);

        SurfVoiceBuilder playBuilder = new SurfVoiceBuilder(playId);
        playBuilder.setChannel(mixerId, false,
                localPayloadId, // inPayloadId
                localPayloadId,  // outpayloadId
                playPort,
                "0.0.0.0", 0);
        json = playBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, playId, json);

        int bgId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_PAR_BG);
        int bgPort =  SurfChannelManager.getUdpPort(bgId);

        SurfVoiceBuilder bgBuilder = new SurfVoiceBuilder(bgId);
        bgBuilder.setChannel(mixerId, false,
                localPayloadId, // inPayloadId
                localPayloadId,  // outpayloadId
                bgPort,
                "0.0.0.0", 0);
        json = bgBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, bgId, json);

        return true;
    }

 }
