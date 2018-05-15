package x3.player.mru.session.StateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.AppInstance;
import x3.player.mru.config.SurfConfig;
import x3.player.mru.rmqif.messages.FileData;
import x3.player.mru.room.RoomInfo;
import x3.player.mru.room.RoomManager;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionState;
import x3.player.mru.surfif.module.SurfChannelManager;
import x3.player.mru.surfif.module.SurfConnectionManager;
import x3.player.mru.surfif.module.SurfVoiceBuilder;

public class UpdateStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(UpdateStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo, Object arg) {
        if (sessionInfo == null) {
            return;
        }

        // NOT to change the status value with UPDATE

        //
        // TODO
        //
        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
        if (roomInfo == null) {
            logger.error("[{}] No roomInfo found", sessionInfo.getSessionId());
            return;
        }

        if (roomInfo.getGroupId() < 0) {
            logger.error("[{}] No channel group found", sessionInfo.getSessionId());
            return;
        }

        if (arg != null && arg instanceof Boolean) {
            boolean isLow = (Boolean)arg;
            if (roomInfo.isVolumeMin() != isLow) {
                roomInfo.setVolumeMin(isLow);
                updateVolume(sessionInfo, roomInfo, isLow);
            }
        }


    }

    private boolean updateVolume(SessionInfo sessionInfo, RoomInfo roomInfo, boolean isLow) {
        if (sessionInfo == null) {
            return false;
        }

        logger.debug("{} Update play volume: isLow {}", sessionInfo.getSessionId(), isLow);

        String json;
        int groupId = roomInfo.getGroupId();
        int mixerId = roomInfo.getMixerId();

        if (groupId < 0 || mixerId < 0) {
            return false;
        }

        SurfConnectionManager connectionManager = SurfConnectionManager.getInstance();

        SurfConfig surfConfig = AppInstance.getInstance().getConfig().getSurfConfig();

        // Creates bg & play channels
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
        if (!isLow) {
            bgBuilder.setAgc(-15, -10);
        }
        else {
            bgBuilder.disableAgc();
        }

        json = bgBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, bgId, json);

        int mentId = SurfChannelManager.getReqToolId(groupId, SurfChannelManager.TOOL_ID_PAR_MENT);
        int mentPort =  SurfChannelManager.getUdpPort(mentId);

        SurfVoiceBuilder mentBuilder = new SurfVoiceBuilder(mentId);
        mentBuilder.setChannel(mixerId,
                surfConfig.getInternalPayload(), // inPayloadId
                surfConfig.getInternalPayload(),  // outpayloadId
                mentPort,
                "127.0.0.1",
                SurfChannelManager.getUdpPort(groupId, SurfChannelManager.TOOL_ID_MENT));
        mentBuilder.setCoder(surfConfig.getInternalCodec(), surfConfig.getInternalCodec(),
                surfConfig.getInternalSampleRate(), surfConfig.getInternalSampleRate(),
                false);

        if (!roomInfo.isVoice()) {
            mentBuilder.setAgc(-15, -10);
        }
        else {
            mentBuilder.disableAgc();
        }

        json = mentBuilder.build();

        connectionManager.addSendQueue(sessionInfo.getSessionId(), groupId, mentId, json);

        return true;
    }

}
