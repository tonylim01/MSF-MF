package x3.player.mru.surfif.handler;

import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.AppInstance;
import x3.player.mru.common.JsonMessage;
import x3.player.mru.config.AmfConfig;
import x3.player.mru.rmqif.handler.RmqProcDtmfDetectReq;
import x3.player.mru.rmqif.handler.RmqProcOutgoingCommandReq;
import x3.player.mru.rmqif.handler.RmqProcOutgoingHangupReq;
import x3.player.mru.rmqif.messages.FileData;
import x3.player.mru.room.RoomInfo;
import x3.player.mru.room.RoomManager;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionManager;
import x3.player.mru.session.SessionState;
import x3.player.mru.session.SessionStateManager;
import x3.player.mru.surfif.messages.SurfMsgToolInf;
import x3.player.mru.surfif.messages.SurfMsgToolInfData;
import x3.player.mru.surfif.module.SurfPlayInfo;
import x3.player.mru.surfif.module.SurfPlayManager;

public class SurfProcToolInf {
    private static final Logger logger = LoggerFactory.getLogger(SurfProcToolInf.class);
    private static final boolean isLogging = false;

    public SurfMsgToolInf parse(JsonElement element) {
        if (element == null) {
            return null;
        }

        SurfMsgToolInf msg = null;

        JsonMessage<SurfMsgToolInf> parser = new JsonMessage<>(SurfMsgToolInf.class);
        msg = parser.parse(element);

        if (msg == null) {
            return null;
        }

        if (msg.getInfType() == null || msg.getData() == null) {
            return msg;
        }

        SurfMsgToolInfData data = msg.getData();
        if (isLogging)
            logger.debug("SysInf type {} data type {}", msg.getInfType(), data.getType());

        if (data.getType().equals("play_started")) {
            parsePlayStarted(data);
        }
        else if (data.getType().equals("end_of_file")) {
            parsePlayEnd(data);
        }
        else if (data.getType().equals("end_of_playlist")) {
            // Nothing to do
        }
        else if (data.getType().equals("RTP_event_detected")) {
            parseRtpEventDetected(data);
        }
        else if (data.getType().equals("RTP_event_ended")) {
            // Nothing to do
        }
        else {
            logger.warn("SysInf: Unknown data type {}", data.getType());
        }

        return msg;
    }

    private boolean parsePlayStarted(SurfMsgToolInfData data) {
        if (data == null) {
            return false;
        }

        if (data.getAppInfo() == null) {
            return false;
        }

        SurfPlayInfo playInfo = SurfPlayManager.getInstance().getData(data.getAppInfo());
        if (playInfo == null) {
            logger.error("Play data not found. appInfo=[{}]", data.getAppInfo());
            return false;
        }

        SessionInfo sessionInfo = SessionManager.getInstance().getSession(playInfo.getSessionId());
        if (sessionInfo == null) {
            logger.warn("[{}] Session not found", playInfo.getSessionId());
            return false;
        }

        SurfPlayManager.getInstance().updateData(data.getAppInfo(), true);

        SessionStateManager.getInstance().setState(sessionInfo.getSessionId(), SessionState.UPDATE, (Boolean)true);

        return true;
    }

    private boolean parsePlayEnd(SurfMsgToolInfData data) {
        if (data == null) {
            return false;
        }

        if (data.getAppInfo() == null) {
            logger.warn("ToolInf data: appInfo not found");
            return false;
        }

        if (data.getFilename() == null) {
            logger.warn("ToolInf data: filename not found");
            return false;
        }

        SurfPlayInfo playInfo = SurfPlayManager.getInstance().getData(data.getAppInfo());
        if (playInfo == null) {
            logger.error("Play data not found. appInfo=[{}]", data.getAppInfo());
            return false;
        }

        SessionInfo sessionInfo = SessionManager.getInstance().getSession(playInfo.getSessionId());
        if (sessionInfo == null) {
            logger.warn("[{}] Session not found", playInfo.getSessionId());
            return false;
        }

        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());

        SurfPlayManager.getInstance().updateData(data.getAppInfo(), false);

        int channel = playInfo.getChannel();
        if (channel == FileData.CHANNEL_BGM) {
            if (roomInfo != null) {
                roomInfo.setBgm(false);
            }
        }
        else {
            if (roomInfo != null) {
                roomInfo.setMent(false);
            }
        }

        SurfPlayManager.getInstance().removeData(data.getAppInfo());
        sessionInfo.removePlayId(data.getAppInfo());

        logger.debug("[{}] Current play list: size [{}]", sessionInfo.getSessionId(),
                (sessionInfo.getPlayIds() != null) ? sessionInfo.getPlayIds().size() : 0);

        if (sessionInfo.getPlayIds()  != null) {
            for (String playId: sessionInfo.getPlayIds()) {
                logger.debug("[{}] Current remaining play id [{}]", sessionInfo.getSessionId(), playId);
            }
        }

        SessionStateManager.getInstance().setState(sessionInfo.getSessionId(), SessionState.UPDATE, (Boolean) false);

        String fromQueue = null;
        if (sessionInfo.getFromQueue() != null) {
            fromQueue = sessionInfo.getFromQueue();
        }
        else {
            AmfConfig config = AppInstance.getInstance().getConfig();

            logger.warn("[{}] Session has null fromQueue. Set to default [{}]", data.getAppInfo(), config.getMcudName());
            fromQueue = config.getMcudName();
        }


        //
        // Sends command_req with play_done
        //
        RmqProcOutgoingCommandReq cmdReq = new RmqProcOutgoingCommandReq(sessionInfo.getSessionId(), null);

        cmdReq.setPlayDone(channel);
        cmdReq.send(fromQueue);

        return true;
    }

    private boolean parseRtpEventDetected(SurfMsgToolInfData data) {
        if (data == null) {
            return false;
        }

        if (data.getAppInfo() == null) {
            logger.warn("ToolInf data: appInfo not found");
            return false;
        }

        if (data.getEvent() == null) {
            logger.warn("ToolInf data: event not found");
            return false;
        }

        SessionInfo sessionInfo = SessionManager.getInstance().getSession(data.getAppInfo());
        if (sessionInfo == null) {
            logger.warn("[{}] Session not found", data.getAppInfo());
            return false;
        }

        AmfConfig config = AppInstance.getInstance().getConfig();

        logger.info("[{}] DTMF detected: mdn [{}] [{}]", sessionInfo.getSessionId(), sessionInfo.getFromNo(), data.getEvent());

        RmqProcDtmfDetectReq detReq = new RmqProcDtmfDetectReq(sessionInfo.getSessionId(), null);
        detReq.setDtmfInfo(sessionInfo.getFromNo(), data.getEvent());
        detReq.send(config.getRmqAcswf());

        return true;
    }

}
