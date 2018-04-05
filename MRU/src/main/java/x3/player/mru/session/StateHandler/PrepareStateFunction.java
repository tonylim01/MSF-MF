package x3.player.mru.session.StateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.core.sdp.SdpInfo;
import x3.player.mru.room.RoomInfo;
import x3.player.mru.room.RoomManager;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionManager;
import x3.player.mru.session.SessionState;
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

        // Step #1) Creates local resource to relay and convert RTP -> UDP
        openLocalResource(sessionInfo);

        // Step #2) Allocates 5 channels on the Surf
        allocateDspResources(sessionInfo);

        // Step #3) Makes a conference channel at the 1st one which has 4 dominants

        // Step #4) Makes caller & callee channel with same privileges

        // Step #5) Makes two playing channels

        // Step #6) Connects 4 channels into the conference

        // Step #7) Connects the above local resource to the caller channel


    }

    /**
     * Opens local udp server to receive rtp packets
     * @param sessionInfo
     * @return
     */
    private boolean openLocalResource(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return false;
        }

        logger.debug("{} Open local resources", sessionInfo.getSessionId());
        //
        // TODO
        //
        // Start of Demo Service
        UdpRelayManager udpRelayManager = UdpRelayManager.getInstance();
        udpRelayManager.openServer(sessionInfo.getSessionId(), sessionInfo.getLocalPort());

        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
        if (roomInfo == null) {
            logger.error("[{}] No roomInfo found", sessionInfo.getSessionId());
            return false;
        }

        String otherSessionId = roomInfo.getOtherSession(sessionInfo.getSessionId());
        if (otherSessionId != null) {
            logger.info("[{}] Connected to session [{}]", sessionInfo.getSessionId(), otherSessionId);

            SessionInfo otherSessionInfo = SessionManager.findSession(otherSessionId);
            if (otherSessionInfo == null) {
                logger.warn("[{}] No sessionInfo found", otherSessionId);
                return false;
            }

            SdpInfo otherRemoteSdpInfo = otherSessionInfo.getSdpInfo();
            udpRelayManager.openClient(sessionInfo.getSessionId(), otherRemoteSdpInfo.getRemoteIp(), otherRemoteSdpInfo.getRemotePort());

            logger.debug("[{}] Make connection: local [{}] to [{}:{}]", sessionInfo.getSessionId(),
                    sessionInfo.getLocalPort(), otherRemoteSdpInfo.getRemoteIp(), otherRemoteSdpInfo.getRemotePort());

        }
        // End of Demo service

        return true;
    }

    private boolean allocateDspResources(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return false;
        }

        logger.debug("{} Allocates DSP resources", sessionInfo.getSessionId());

        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
        if (roomInfo == null) {
            logger.error("[{}] No roomInfo found", sessionInfo.getSessionId());
            return false;
        }

        SurfChannelManager channelManager = SurfChannelManager.getInstance();
        int groupId = channelManager.getIdleChannelGroup();

        if (groupId < 0) {
            logger.error("Failed to find idle channel group");
            return false;
        }

        SurfConnectionManager connectionManager = SurfConnectionManager.getInstance();

        int mixerId = channelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_MIXER);
        int callerId = channelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_CALLER);
        int calleeId = channelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_CALLEE);
        int bgId = channelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_BG);
        int parId = channelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_PAR);
        int playId = channelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_PLAY);

        // Creates a mixer
        String json = channelManager.buildCreateVoiceMixer(mixerId);
        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, mixerId, json);

        // Creates a caller
        json = channelManager.buildCreateVoiceChannel(callerId, mixerId);
        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, callerId, json);

        // Creates a bg
        json = channelManager.buildCreateVoiceChannel(callerId, bgId);
        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, bgId, json);

        // Creates a play
        json = channelManager.buildCreateVoiceChannel(callerId, playId);
        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, playId, json);

        return true;
    }

}
