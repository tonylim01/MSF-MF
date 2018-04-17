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

public class PlayStateFunction {
    private static final Logger logger = LoggerFactory.getLogger(PlayStateFunction.class);

    protected boolean stopPlay(SessionInfo sessionInfo, RoomInfo roomInfo, int toolId) {
        if (sessionInfo == null) {
            logger.error("No session");
            return false;
        }

        if (roomInfo == null) {
            logger.error("[{}] Invalid argument", sessionInfo.getSessionId());
            return false;
        }

        logger.debug("[{}] Stop play: toolId [{}]", sessionInfo.getSessionId(), toolId);

        String json;
        int groupId = roomInfo.getGroupId();
        int mixerId = roomInfo.getMixerId();

        if (groupId < 0 || mixerId < 0) {
            return false;
        }

        SurfConnectionManager connectionManager = SurfConnectionManager.getInstance();

        // Pauses playing
        int stopId = SurfChannelManager.getReqToolId(groupId, toolId);

        SurfPlayBuilder stopBuilder = new SurfPlayBuilder(stopId);
        stopBuilder.setPlayPause();
        json = stopBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, stopId, json);

        // Clears play state
        SurfPlayBuilder clearBuilder = new SurfPlayBuilder(stopId);
        clearBuilder.setPlayListClear();
        json = clearBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, stopId, json);

        return true;
    }
}
