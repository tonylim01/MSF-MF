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
import x3.player.mru.session.SessionManager;
import x3.player.mru.session.SessionState;
import x3.player.mru.simulator.BiUdpRelayManager;
import x3.player.mru.simulator.UdpRelayManager;
import x3.player.mru.surfif.module.SurfChannelManager;
import x3.player.mru.surfif.module.SurfConnectionManager;

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
        //
        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
        if (roomInfo == null) {
            logger.error("[{}] No roomInfo found", sessionInfo.getSessionId());
            return;
        }

        if (roomInfo.getGroupId() < 0) {
            allocateGroup(roomInfo);
            // Step #1) Creates a voice mixer
            openMixerResource(roomInfo, sessionInfo.getSessionId());

            // Step #2) Create play & bg channels and connects them to the mixer
            openServiceResource(sessionInfo, roomInfo);
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

        logger.debug("{} Open relay resources", sessionInfo.getSessionId());

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
        int parPort = SurfChannelManager.getUdpPort(roomInfo.getGroupId(), SurfChannelManager.TOOL_ID_PAR);

        udpRelayManager.openSrcServer(sessionInfo.getSessionId(), sessionInfo.getSrcLocalPort());
        udpRelayManager.openSrcClient(sessionInfo.getSessionId(),
                surfConfig.getSurfIp(), parPort);

        // TODO
        // srcLocalPort -> AIIS as well as -> Surf par
        //


        // dstLocalPort -> Surf caller
        int callerPort = SurfChannelManager.getUdpPort(roomInfo.getGroupId(), SurfChannelManager.TOOL_ID_CG_TX);

        udpRelayManager.openDstServer(sessionInfo.getSessionId(), sessionInfo.getDstLocalPort());
        udpRelayManager.openDstClient(sessionInfo.getSessionId(),
                surfConfig.getSurfIp(), callerPort);

        return true;
    }

    private boolean allocateGroup(RoomInfo roomInfo) {
        if (roomInfo == null) {
            return false;
        }

        int groupId = roomInfo.getGroupId();
        roomInfo.setGroupId(groupId);

        logger.debug("({}) Allocates new group [{}]", roomInfo.getRoomId(), groupId);

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

        // Creates a mixer
        int mixerId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_MIXER);
        roomInfo.setMixerId(mixerId);

        String json = channelManager.buildCreateVoiceMixer(mixerId);
        connectionManager.addSendQueue(sessionId, groupId, mixerId, json);

        return true;
    }

    private boolean openCallerResource(SessionInfo sessionInfo, RoomInfo roomInfo) {
        if (sessionInfo == null) {
            return false;
        }

        logger.debug("{} Allocates caller DSP resources", sessionInfo.getSessionId());

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
        int parId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_PAR);

        int rxPort = SurfChannelManager.getUdpPort(cgRxId);
        int txPort = ((rxPort << 16) & 0xffff0000) + SurfChannelManager.getUdpPort(cgTxId);


        // Creates a caller as p2p mode (RX channel: remote -> local)
        json = channelManager.buildCreateVoiceChannel(cgRxId, -1,
                sdpInfo.getPayloadId(), // inPayloadId
                localPayloadId,  // outpayloadId
                rxPort,
                config.getLocalIpAddress(), sessionInfo.getSrcLocalPort());

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, cgRxId, json);

        // Creates a caller as p2p mode (TX channel: remote <- local)
        json = channelManager.buildCreateVoiceChannel(cgTxId, -1,
                localPayloadId, // inPayloadId
                sdpInfo.getPayloadId(),  // outpayloadId
                txPort,
                sdpInfo.getRemoteIp(), sdpInfo.getRemotePort());

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, cgTxId, json);

        // Creates a mixer's participant as ip mode
        json = channelManager.buildCreateVoiceChannel(parId, mixerId,
                localPayloadId,
                localPayloadId,
                SurfChannelManager.getUdpPort(parId),
                config.getLocalIpAddress(), sessionInfo.getDstLocalPort());
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

        // Creates a callee as ip mode
        json = channelManager.buildCreateVoiceChannel(calleeId, mixerId,
                sdpInfo.getPayloadId(), // inPayloadId
                localPayloadId,  // outpayloadId
                calleePort,
                sdpInfo.getRemoteIp(), sdpInfo.getRemotePort());

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, calleeId, json);

        return true;
    }

    private boolean openServiceResource(SessionInfo sessionInfo, RoomInfo roomInfo) {
        if (sessionInfo == null) {
            return false;
        }

        logger.debug("{} Allocates service DSP resources", sessionInfo.getSessionId());

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
        int bgId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_BG);
        int playId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_PLAY);

        int bgPort =  SurfChannelManager.getUdpPort(bgId);
        int playPort =  SurfChannelManager.getUdpPort(playId);

        json = channelManager.buildCreateVoiceChannel(bgId, mixerId,
                localPayloadId, // inPayloadId
                localPayloadId,  // outpayloadId
                bgPort,
                "0.0.0.0", 0);

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, bgId, json);

        json = channelManager.buildCreateVoiceChannel(playId, mixerId,
                localPayloadId, // inPayloadId
                localPayloadId,  // outpayloadId
                playPort,
                "0.0.0.0", 0);

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, playId, json);

        return true;
    }
}
