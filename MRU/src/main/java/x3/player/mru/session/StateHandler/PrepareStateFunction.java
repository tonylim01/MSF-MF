package x3.player.mru.session.StateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.core.sdp.SdpInfo;
import x3.player.mru.AppInstance;
import x3.player.mru.config.AmfConfig;
import x3.player.mru.config.SurfConfig;
import x3.player.mru.rmqif.messages.FileData;
import x3.player.mru.room.RoomInfo;
import x3.player.mru.room.RoomManager;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionState;
import x3.player.mru.session.SessionStateManager;
import x3.player.mru.simulator.BiUdpRelayManager;
import x3.player.mru.surfif.messages.SurfMsgVocoder;
import x3.player.mru.surfif.module.SurfChannelManager;
import x3.player.mru.surfif.module.SurfConnectionManager;
import x3.player.mru.surfif.module.SurfVoiceBuilder;

public class PrepareStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(PrepareStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo, Object arg) {
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

                //
                // TODO: DEMO
                //
                playDemoAudio(sessionInfo, roomInfo);
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

        logger.debug("[{}] Relay: remote ({}:{}) <- local ({})", sessionInfo.getSessionId(),
                surfConfig.getSurfIp(), parPort, sessionInfo.getSrcLocalPort());

        udpRelayManager.openSrcServer(sessionInfo.getSessionId(), sessionInfo.getSrcLocalPort());
        udpRelayManager.openSrcClient(sessionInfo.getSessionId(),
                surfConfig.getSurfIp(), parPort);

        // TODO
        // srcLocalPort -> AIIS as well as -> Surf par
        //


        // dstLocalPort -> Surf caller
        int callerPort = SurfChannelManager.getUdpPort(roomInfo.getGroupId(), SurfChannelManager.TOOL_ID_CG_TX);

        logger.debug("[{}] Relay: remote ({}:{}) <- local ({})", sessionInfo.getSessionId(),
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

        SurfConfig config = AppInstance.getInstance().getConfig().getSurfConfig();

        SurfVoiceBuilder builder = new SurfVoiceBuilder(mixerId);
        builder.setMixer(config.getInternalSampleRate(), 500, 5); // TODO : Temp. value
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
        SurfConfig surfConfig = AppInstance.getInstance().getConfig().getSurfConfig();

        // Creates 3 voice channels
        int cgRxId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_CG_RX);
        int cgTxId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_CG_TX);
        int parId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_PAR_CG);

        int rxPort = SurfChannelManager.getUdpPort(cgRxId);
        int txPort = SurfChannelManager.getUdpPort(cgTxId);

        logger.debug("[{}] CG_RX ptp: remote ({}:{}) -> local ({})", sessionInfo.getSessionId(),
                config.getLocalIpAddress(), sessionInfo.getSrcLocalPort(), rxPort);

        // Creates a caller as p2p mode (RX channel: remote -> local)
        SurfVoiceBuilder rxBuilder = new SurfVoiceBuilder(cgRxId);
        rxBuilder.setChannel(-1,
                sdpInfo.getPayloadId(), // inPayloadId
                surfConfig.getInternalPayload(),  // outpayloadId
                rxPort,
                config.getLocalIpAddress(), sessionInfo.getSrcLocalPort(),
                false);
        rxBuilder.setCoder(surfConfig.getInternalCodec(), SurfMsgVocoder.VOCODER_ALAW, true);
        json = rxBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, cgRxId, json);

        logger.debug("[{}] CG_TX ptp: remote ({}:{}) <- local ({})", sessionInfo.getSessionId(),
                sdpInfo.getRemoteIp(), sdpInfo.getRemotePort(), txPort);

        // Creates a caller as p2p mode (TX channel: remote <- local)
        SurfVoiceBuilder txBuilder = new SurfVoiceBuilder(cgTxId);
        txBuilder.setChannel(-1,
                surfConfig.getInternalPayload(), // inPayloadId
                sdpInfo.getPayloadId(),  // outpayloadId
                txPort,
                sdpInfo.getRemoteIp(), sdpInfo.getRemotePort(),
                false);
        txBuilder.setCoder(SurfMsgVocoder.VOCODER_ALAW, surfConfig.getInternalCodec(), true);
        txBuilder.setOverrideSrcPort(rxPort);
        json = txBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, cgTxId, json);

        int parPort = SurfChannelManager.getUdpPort(parId);

        logger.debug("[{}] CG_par fe: remote ({}:{}) - local ({}) - mixer ({})", sessionInfo.getSessionId(),
                config.getLocalIpAddress(), sessionInfo.getDstLocalPort(), parPort, mixerId);

        // Creates a mixer's participant as ip mode
        SurfVoiceBuilder parBuilder = new SurfVoiceBuilder(parId);
        parBuilder.setChannel(mixerId,
                surfConfig.getInternalPayload(),
                surfConfig.getInternalPayload(),
                parPort,
                config.getLocalIpAddress(), sessionInfo.getDstLocalPort(),
                true);
        parBuilder.setCoder(surfConfig.getInternalCodec(), surfConfig.getInternalCodec(), true);
        json = parBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, parId, json);

        // Add participants
        SurfVoiceBuilder addBuilder = new SurfVoiceBuilder(mixerId);
        addBuilder.setParticipant(parId, parId);
        json = addBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, mixerId, json);

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

        SdpInfo sdpInfo = sessionInfo.getSdpInfo();
        SurfConfig surfConfig = AppInstance.getInstance().getConfig().getSurfConfig();

        // Creates one voice channel
        int calleeId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_CD);
        int calleePort = SurfChannelManager.getUdpPort(calleeId);

        logger.debug("[{}] CD_par fe: remote ({}:{}) - local ({}) - mixer ({})", sessionInfo.getSessionId(),
                sdpInfo.getRemoteIp(), sdpInfo.getRemotePort(), calleePort, mixerId);

        // Creates a callee as ip mode
        SurfVoiceBuilder builder = new SurfVoiceBuilder(calleeId);
        builder.setChannel(mixerId,
                surfConfig.getInternalPayload(),  // outpayloadId
                sdpInfo.getPayloadId(), // inPayloadId
                calleePort,
                sdpInfo.getRemoteIp(), sdpInfo.getRemotePort(),
                true);
//        builder.setCoder(surfConfig.getInternalCodec(), SurfMsgVocoder.VOCODER_ALAW, true);
        builder.setCoder(SurfMsgVocoder.VOCODER_ALAW, surfConfig.getInternalCodec(), true);
        json = builder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, calleeId, json);

        // Add participants
        SurfVoiceBuilder addBuilder = new SurfVoiceBuilder(mixerId);
        addBuilder.setParticipant(calleeId, calleeId);
        json = addBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, mixerId, json);


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

        SdpInfo sdpInfo = sessionInfo.getSdpInfo();
        SurfConfig surfConfig = AppInstance.getInstance().getConfig().getSurfConfig();

        // Creates bg & play channels
        int playId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_PAR_MENT);
        int playPort =  SurfChannelManager.getUdpPort(playId);

        SurfVoiceBuilder playBuilder = new SurfVoiceBuilder(playId);
        playBuilder.setChannel(mixerId,
                surfConfig.getInternalPayload(), // inPayloadId
                surfConfig.getInternalPayload(),  // outpayloadId
                playPort,
                "127.0.0.1",
                SurfChannelManager.getUdpPort(groupId, SurfChannelManager.TOOL_ID_MENT),
                false);
        playBuilder.setCoder(surfConfig.getInternalCodec(), surfConfig.getInternalCodec(), false);
        json = playBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, playId, json);

        // Add participants
        SurfVoiceBuilder par1Builder = new SurfVoiceBuilder(mixerId);
        par1Builder.setParticipant(playId, playId);
        json = par1Builder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, mixerId, json);

        int bgId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_PAR_BG);
        int bgPort =  SurfChannelManager.getUdpPort(bgId);

        SurfVoiceBuilder bgBuilder = new SurfVoiceBuilder(bgId);
        bgBuilder.setChannel(mixerId,
                surfConfig.getInternalPayload(), // inPayloadId
                surfConfig.getInternalPayload(),  // outpayloadId
                bgPort,
                "127.0.0.1",
                SurfChannelManager.getUdpPort(groupId, SurfChannelManager.TOOL_ID_BG),
                false);
        bgBuilder.setCoder(surfConfig.getInternalCodec(), surfConfig.getInternalCodec(), false);
//        bgBuilder.setAgc(-29, -28);   // TODO: TEST
        json = bgBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, bgId, json);

        // Add participants
        SurfVoiceBuilder par2Builder = new SurfVoiceBuilder(mixerId);
        par2Builder.setParticipant(bgId, bgId);
        json = par2Builder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, mixerId, json);

        return true;
    }

    private boolean playDemoAudio(SessionInfo sessionInfo, RoomInfo roomInfo) {
        if (sessionInfo == null) {
            return false;
        }

        logger.debug("[{}] Play demo audio", sessionInfo.getSessionId());

        String json;
        int groupId = roomInfo.getGroupId();
        int mixerId = roomInfo.getMixerId();

        if (groupId < 0 || mixerId < 0) {
            return false;
        }

        FileData file = new FileData();
        file.setChannel(FileData.CHANNEL_BGM);
        file.setPlayFile("/home/amf/bin/Heize_rain_and.wav");
        SessionStateManager.getInstance().setState(sessionInfo.getSessionId(), SessionState.PLAY_START, file);

        return true;
    }
 }
