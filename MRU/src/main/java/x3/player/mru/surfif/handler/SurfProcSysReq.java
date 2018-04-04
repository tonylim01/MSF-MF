package x3.player.mru.surfif.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.AppInstance;
import x3.player.mru.common.JsonMessage;
import x3.player.mru.config.SurfConfig;
import x3.player.mru.surfif.messages.SurfMsgConnect;
import x3.player.mru.surfif.messages.SurfMsgSetConfigStatus;
import x3.player.mru.surfif.messages.SurfMsgSysReq;
import x3.player.mru.surfif.module.SurfConnectionManager;
import x3.player.mru.surfif.module.SurfJsonMessage;
import x3.player.mru.surfif.types.SurfConstant;

public class SurfProcSysReq {
    private static final Logger logger = LoggerFactory.getLogger(SurfProcSysReq.class);

    private static final int STATUS_PERIOD = 1000;  // millisec

    private SurfMsgSysReq sysReq = null;

    public String build(boolean isEnable) {

        SurfConfig config = AppInstance.getInstance().getConfig().getSurfConfig();

        if (config == null) {
            return null;
        }

        int reqId = SurfConnectionManager.getInstance().newReqId();

        SurfMsgSysReq msg = new SurfMsgSysReq();

        msg.setReqId(reqId);
        msg.setReqType(SurfConstant.ReqType.SET_CONFIG);

        SurfMsgSetConfigStatus status = new SurfMsgSetConfigStatus(1);
        status.add("all", isEnable ? STATUS_PERIOD : 0);
        msg.setData(status);

        SurfJsonMessage<SurfMsgSysReq> jsonMessage = new SurfJsonMessage<>(SurfMsgSysReq.class);
        String jsonStr = jsonMessage.build(SurfMsgSysReq.MSG_NAME, msg);

        return jsonStr;

    }

}
