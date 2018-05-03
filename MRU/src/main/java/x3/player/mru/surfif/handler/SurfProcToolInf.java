package x3.player.mru.surfif.handler;

import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.AppInstance;
import x3.player.mru.common.JsonMessage;
import x3.player.mru.config.AmfConfig;
import x3.player.mru.rmqif.handler.RmqProcOutgoingCommandReq;
import x3.player.mru.rmqif.handler.RmqProcOutgoingHangupReq;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionManager;
import x3.player.mru.surfif.messages.SurfMsgToolInf;
import x3.player.mru.surfif.messages.SurfMsgToolInfData;

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
            // Nothing to do
        }
        else if (data.getType().equals("end_of_playlist")) {
            parsePlayEnd(data);
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

        SessionInfo sessionInfo = SessionManager.getInstance().getSession(data.getAppInfo());
        if (sessionInfo == null) {
            logger.warn("[{}] Session not found", data.getAppInfo());
            return false;
        }

        // TODO

        return true;
    }

    private boolean parsePlayEnd(SurfMsgToolInfData data) {
        if (data == null) {
            return false;
        }

        if (data.getAppInfo() == null) {
            return false;
        }

        SessionInfo sessionInfo = SessionManager.getInstance().getSession(data.getAppInfo());
        if (sessionInfo == null) {
            logger.warn("[{}] Session not found", data.getAppInfo());
            return false;
        }

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
        cmdReq.send(fromQueue);

        return true;
    }

}
