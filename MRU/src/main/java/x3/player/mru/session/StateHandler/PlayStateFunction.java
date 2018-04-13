package x3.player.mru.session.StateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.room.RoomInfo;
import x3.player.mru.room.RoomManager;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionState;

public class PlayStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(PlayStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return;
        }

        logger.debug("{} PLAY state", sessionInfo.getSessionId());

        if (sessionInfo.getServiceState() != SessionState.PLAY) {
            sessionInfo.setServiceState(SessionState.PLAY);
        }

        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
        if (roomInfo == null) {
            logger.error("[{}] No roomInfo found", sessionInfo.getSessionId());
            return;
        }

        if (roomInfo.getGroupId() < 0) {
            //
            // TODO : error
            //
            return;
        }

    }

 }
