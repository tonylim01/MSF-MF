package x3.player.mru.session.StateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.core.sdp.SdpInfo;
import x3.player.mru.rmqif.messages.FileData;
import x3.player.mru.room.RoomInfo;
import x3.player.mru.room.RoomManager;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionState;
import x3.player.mru.surfif.module.SurfChannelManager;
import x3.player.mru.surfif.module.SurfConnectionManager;

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
            logger.error("[{}] No channel group found", sessionInfo.getSessionId());
            return;
        }

        playFile(sessionInfo, roomInfo);
    }

    private boolean playFile(SessionInfo sessionInfo, RoomInfo roomInfo) {
        if (sessionInfo == null) {
            return false;
        }

        FileData fileData = sessionInfo.getFileData();

        if (fileData == null) {
            logger.warn("[{}] No file data", sessionInfo.getSessionId());
            return false;
        }

        logger.debug("[{}] Play file", sessionInfo.getSessionId());

        String json;
        int groupId = roomInfo.getGroupId();
        int mixerId = roomInfo.getMixerId();

        if (groupId < 0 || mixerId < 0) {
            return false;
        }

        SurfConnectionManager connectionManager = SurfConnectionManager.getInstance();
        SurfChannelManager channelManager = SurfChannelManager.getInstance();



        // Creates bg & play channels
        /*
        int playId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_PAR_PLAY);
        int playPort =  SurfChannelManager.getUdpPort(playId);

        json = channelManager.buildCreateVoiceChannel(playId, mixerId, false,
                localPayloadId, // inPayloadId
                localPayloadId,  // outpayloadId
                playPort,
                "0.0.0.0", 0);

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, playId, json);

        int bgId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_PAR_BG);
        int bgPort =  SurfChannelManager.getUdpPort(playId);

        json = channelManager.buildCreateVoiceChannel(bgId, mixerId, false,
                localPayloadId, // inPayloadId
                localPayloadId,  // outpayloadId
                bgPort,
                "0.0.0.0", 0);

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, bgId, json);
        */
        return true;
    }

}
