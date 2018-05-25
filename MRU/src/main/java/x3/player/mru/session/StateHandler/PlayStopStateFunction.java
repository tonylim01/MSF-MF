package x3.player.mru.session.StateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.rmqif.messages.FileData;
import x3.player.mru.room.RoomInfo;
import x3.player.mru.room.RoomManager;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionState;
import x3.player.mru.session.SessionStateManager;
import x3.player.mru.surfif.module.*;

public class PlayStopStateFunction extends PlayStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(PlayStopStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo, Object arg) {
        if (sessionInfo == null) {
            return;
        }

        logger.debug("{} PLAY stop state", sessionInfo.getSessionId());

        if (sessionInfo.getServiceState() != SessionState.PLAY_START) {
            sessionInfo.setServiceState(SessionState.PLAY_START);
        }

        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
        if (roomInfo == null) {
            logger.error("[{}] No roomInfo found", sessionInfo.getSessionId());
            return;
        }

        if (roomInfo.getGroupId() < 0) {
            logger.error("[{}] No channel group found", sessionInfo.getSessionId());
            return;
        }

        if (arg != null && arg instanceof FileData) {
            FileData fileData = (FileData) arg;

            if (sessionInfo.getPlayIds() != null) {
                for (String playId: sessionInfo.getPlayIds()) {
                    SurfPlayInfo playInfo = SurfPlayManager.getInstance().getData(playId);
                    if (playInfo != null) {
                        if (fileData.getChannel() == playInfo.getChannel()) {
                            SurfPlayManager.getInstance().removeData(playId);
                            sessionInfo.removePlayId(playId);
                            break;
                        }
                    }
                }
            }

            if (fileData.getChannel() == FileData.CHANNEL_BGM && sessionInfo.isBgmPlaying()) {
                stopPlay(sessionInfo, roomInfo, SurfChannelManager.TOOL_ID_BG);
            } else if (fileData.getChannel() == FileData.CHANNEL_MENT && sessionInfo.isMentPlaying()) {
                stopPlay(sessionInfo, roomInfo, SurfChannelManager.TOOL_ID_MENT);
            }

            SessionStateManager.getInstance().setState(sessionInfo.getSessionId(), SessionState.UPDATE, (Boolean)false);

        } else {
            logger.error("[{}] Invalid file data");
        }
    }
}
