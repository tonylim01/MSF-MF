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

public class PrepareStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(PrepareStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return;
        }

        if (sessionInfo.getServiceState() != SessionState.PREPARE) {
            sessionInfo.setServiceState(SessionState.PREPARE);
        }

        openLocalResource(sessionInfo);
        //
        // TODO
        //

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

}
