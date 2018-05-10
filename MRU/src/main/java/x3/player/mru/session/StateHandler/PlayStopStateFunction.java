package x3.player.mru.session.StateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.rmqif.messages.FileData;
import x3.player.mru.room.RoomInfo;
import x3.player.mru.room.RoomManager;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionState;
import x3.player.mru.surfif.module.SurfChannelManager;
import x3.player.mru.surfif.module.SurfConnectionManager;
import x3.player.mru.surfif.module.SurfPlayBuilder;

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
            if (fileData.getChannel() == FileData.CHANNEL_BGM && sessionInfo.isBgmPlaying()) {
                stopPlay(sessionInfo, roomInfo, SurfChannelManager.TOOL_ID_BG);
            } else if (fileData.getChannel() == FileData.CHANNEL_MENT && sessionInfo.isMentPlaying()) {
                stopPlay(sessionInfo, roomInfo, SurfChannelManager.TOOL_ID_MENT);
            }
        } else {
            logger.error("[{}] Invalid file data");
        }
    }
}
