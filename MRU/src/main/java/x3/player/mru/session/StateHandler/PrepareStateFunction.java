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
//                playDemoAudio(sessionInfo, roomInfo);

            }

            if (sessionInfo.isCaller()) {
                // Step #3) Creates 3 channels used by a caller
                openCallerResource(sessionInfo, roomInfo);
                // Step #4) Creates local relaying resource
                openCallerRelayResource(sessionInfo);

                //
                // TODO: TEST AIIFD START
                //
//                sessionInfo.setAiifName("aiif1_aiifd_u");
//                SessionStateManager.getInstance().setState(sessionInfo.getSessionId(), SessionState.START);

            }
            else {
                // Step #5) Creates one channel for a callee
                openCalleeResource(sessionInfo, roomInfo);
                // Step #6) Creates local relaying resource
                openCalleeRelayResource(sessionInfo);

            }
        }

    }

    /**
     * (RTP) -> (SURF caller) --> (Relay) --> (SURF par)
     *
     * @param sessionInfo
     * @return
     */
    private boolean openCallerRelayResource(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return false;
        }

        logger.debug("[{}] Open caller relay resources", sessionInfo.getSessionId());

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

        logger.debug("[{}] Caller Relay: remote ({}:{}) <- local ({})", sessionInfo.getSessionId(),
                surfConfig.getSurfIp(), parPort, sessionInfo.getSrcLocalPort());

        udpRelayManager.openSrcServer(sessionInfo.getSessionId(), sessionInfo.getSrcLocalPort());
        udpRelayManager.openSrcClient(sessionInfo.getSessionId(),
                sdpInfo.getRemoteIp(), sdpInfo.getRemotePort());

        // dstLocalPort -> Surf caller
        int callerPort = SurfChannelManager.getUdpPort(roomInfo.getGroupId(), SurfChannelManager.TOOL_ID_CG_TX);

        logger.debug("[{}] Caller Relay: remote ({}:{}) <- local ({})", sessionInfo.getSessionId(),
                surfConfig.getSurfIp(), callerPort, sessionInfo.getDstLocalPort());

        udpRelayManager.openDstServer(sessionInfo.getSessionId(), sessionInfo.getDstLocalPort());
        udpRelayManager.openDstClient(sessionInfo.getSessionId(),
                surfConfig.getSurfIp(), parPort);

        return true;
    }

    /**
     * (RTP) -> (SURF callee) --> (Relay) --> (SURF par)
     *
     * @param sessionInfo
     * @return
     */
    private boolean openCalleeRelayResource(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return false;
        }

        logger.debug("[{}] Open callee relay resources", sessionInfo.getSessionId());

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
        int parPort = SurfChannelManager.getUdpPort(roomInfo.getGroupId(), SurfChannelManager.TOOL_ID_PAR_CD);

        logger.debug("[{}] Callee Relay: remote ({}:{}) <- local ({})", sessionInfo.getSessionId(),
                surfConfig.getSurfIp(), parPort, sessionInfo.getSrcLocalPort());

        udpRelayManager.openSrcServer(sessionInfo.getSessionId(), sessionInfo.getSrcLocalPort());
        udpRelayManager.openSrcClient(sessionInfo.getSessionId(),
                sdpInfo.getRemoteIp(), sdpInfo.getRemotePort());

        // dstLocalPort -> Surf caller
        int callerPort = SurfChannelManager.getUdpPort(roomInfo.getGroupId(), SurfChannelManager.TOOL_ID_CD_TX);

        logger.debug("[{}] Callee Relay: remote ({}:{}) <- local ({})", sessionInfo.getSessionId(),
                surfConfig.getSurfIp(), callerPort, sessionInfo.getDstLocalPort());

        udpRelayManager.openDstServer(sessionInfo.getSessionId(), sessionInfo.getDstLocalPort());
        udpRelayManager.openDstClient(sessionInfo.getSessionId(),
                surfConfig.getSurfIp(), parPort);

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

        SdpInfo sdpInfo = sessionInfo.getSdpInfo();

        int parId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_PAR_CG);
        int parPort = SurfChannelManager.getUdpPort(parId);

        logger.debug("[{}] CG_par fe: remote ({}:{}) - local ({}) - mixer ({})", sessionInfo.getSessionId(),
                config.getLocalIpAddress(), sessionInfo.getDstLocalPort(), parPort, mixerId);

        // Creates a mixer's participant as ip mode
        SurfVoiceBuilder parBuilder = new SurfVoiceBuilder(parId);
        parBuilder.setChannel(mixerId,
                sdpInfo.getPayloadId(),
                sdpInfo.getPayloadId(),
                parPort,
                config.getLocalIpAddress(), sessionInfo.getDstLocalPort());
        parBuilder.setCoder(sdpInfo.getCodecStr(), sdpInfo.getCodecStr(),
                0, 0, true);
//        parBuilder.setAgc(-15, -10);
        if (sdpInfo.getPayload2833() > 0) {
            parBuilder.setDtmf(sessionInfo.getSessionId(), sdpInfo.getPayload2833());
        }

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

        AmfConfig config = AppInstance.getInstance().getConfig();

        SurfConnectionManager connectionManager = SurfConnectionManager.getInstance();

        SdpInfo sdpInfo = sessionInfo.getSdpInfo();

        int parId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_PAR_CD);
        int parPort = SurfChannelManager.getUdpPort(parId);

        logger.debug("[{}] CD_par fe: remote ({}:{}) - local ({}) - mixer ({})", sessionInfo.getSessionId(),
                config.getLocalIpAddress(), sessionInfo.getDstLocalPort(), parPort, mixerId);

        // Creates a mixer's participant as ip mode
        SurfVoiceBuilder parBuilder = new SurfVoiceBuilder(parId);
        parBuilder.setChannel(mixerId,
                sdpInfo.getPayloadId(),
                sdpInfo.getPayloadId(),
                parPort,
                config.getLocalIpAddress(), sessionInfo.getDstLocalPort());
        parBuilder.setCoder(sdpInfo.getCodecStr(), sdpInfo.getCodecStr(),
                0, 0, true);
//        parBuilder.setAgc(-15, -10);
        if (sdpInfo.getPayload2833() > 0) {
            parBuilder.setDtmf(sessionInfo.getSessionId(), sdpInfo.getPayload2833());
        }

        json = parBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, parId, json);

        // Add participants
        SurfVoiceBuilder addBuilder = new SurfVoiceBuilder(mixerId);
        addBuilder.setParticipant(parId, parId);
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
                SurfChannelManager.getUdpPort(groupId, SurfChannelManager.TOOL_ID_MENT));
        playBuilder.setCoder(surfConfig.getInternalCodec(), surfConfig.getInternalCodec(),
                surfConfig.getInternalSampleRate(), surfConfig.getInternalSampleRate(),
                false);
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
                SurfChannelManager.getUdpPort(groupId, SurfChannelManager.TOOL_ID_BG));
        bgBuilder.setCoder(surfConfig.getInternalCodec(), surfConfig.getInternalCodec(),
                surfConfig.getInternalSampleRate(), surfConfig.getInternalSampleRate(),
                false);
//        bgBuilder.setAgc(-30, -20);   // TODO: TEST
//        bgBuilder.setAgc(-10, -8);   // TODO: TEST
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

        int groupId = roomInfo.getGroupId();
        int mixerId = roomInfo.getMixerId();

        if (groupId < 0 || mixerId < 0) {
            return false;
        }

        FileData file = new FileData();
        file.setChannel(FileData.CHANNEL_BGM);
        file.setPlayFile("Heize_rain_and.wav");
        SessionStateManager.getInstance().setState(sessionInfo.getSessionId(), SessionState.PLAY_START, file);

        return true;
    }
 }
