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

public class PlayStartStateFunction extends PlayStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(PlayStartStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo, Object arg) {
        if (sessionInfo == null) {
            return;
        }

        logger.debug("{} PLAY start state", sessionInfo.getSessionId());

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
            FileData fileData = (FileData)arg;
            if (fileData.getChannel() == FileData.CHANNEL_BGM && sessionInfo.isBgmPlaying()) {
                stopPlay(sessionInfo, roomInfo, SurfChannelManager.TOOL_ID_BG);
            }
            else if (fileData.getChannel() == FileData.CHANNEL_MENT && sessionInfo.isMentPlaying()) {
                stopPlay(sessionInfo, roomInfo, SurfChannelManager.TOOL_ID_MENT);
            }

            playFile(sessionInfo, roomInfo, fileData);
        }
        else {
            logger.error("[{}] Invalid file data");
        }
    }

    private boolean playFile(SessionInfo sessionInfo, RoomInfo roomInfo, FileData data) {
        if (sessionInfo == null) {
            logger.error("No session");
            return false;
        }

        if (roomInfo == null || data == null) {
            logger.error("[{}] Invalid argument", sessionInfo.getSessionId());
            return false;
        }

        logger.debug("[{}] Play file: channel [{}] mediaType [{}] file [{}] defVol [{}] mixVol [{}] type [{}]",
                sessionInfo.getSessionId(),
                data.getChannel(), data.getMediaType(), data.getPlayFile(),
                data.getDefVolume(), data.getMixVolume(), data.getPlayType());

        String json;
        int groupId = roomInfo.getGroupId();
        int mixerId = roomInfo.getMixerId();

        if (groupId < 0 || mixerId < 0) {
            return false;
        }

        SurfConnectionManager connectionManager = SurfConnectionManager.getInstance();

        // Creates a play channel which loads a file
        int toolId;
        int dstId;
        if (data.getChannel() == FileData.CHANNEL_BGM) {
            toolId = SurfChannelManager.TOOL_ID_BG;
            dstId = SurfChannelManager.TOOL_ID_PAR_BG;
            sessionInfo.setBgmPlaying(true);
        }
        else {
            toolId = SurfChannelManager.TOOL_ID_MENT;
            dstId = SurfChannelManager.TOOL_ID_PAR_MENT;
            sessionInfo.setMentPlaying(true);
        }

        int fileId = SurfChannelManager.getReqToolId(groupId, toolId);
        int parId = SurfChannelManager.getReqToolId(groupId, dstId);

        SurfPlayBuilder channelBuilder = new SurfPlayBuilder(fileId);
        channelBuilder.setFileReader(parId);
        json = channelBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, fileId, json);

        // Sets a filename to play
        SurfPlayBuilder fileBuilder = new SurfPlayBuilder(fileId);
        fileBuilder.setPlayListAppend(data.getPlayFile());
        json = fileBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, fileId, json);

        // Starts playing
        SurfPlayBuilder playBuilder = new SurfPlayBuilder(fileId);
        playBuilder.setPlayStart();
        json = playBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, fileId, json);

        return true;
    }

}
