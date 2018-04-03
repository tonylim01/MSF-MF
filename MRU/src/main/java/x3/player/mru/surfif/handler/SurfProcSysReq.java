package x3.player.mru.surfif.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.AppInstance;
import x3.player.mru.common.JsonMessage;
import x3.player.mru.config.SurfConfig;
import x3.player.mru.surfif.messages.SurfMsgSysReq;
import x3.player.mru.surfif.module.SurfConnectionManager;
import x3.player.mru.surfif.types.SurfConstant;

public class SurfProcSysReq {
    private static final Logger logger = LoggerFactory.getLogger(SurfProcSysReq.class);

    private SurfMsgSysReq sysReq = null;

    public String build() {

        SurfConfig config = AppInstance.getInstance().getConfig().getSurfConfig();

        if (config == null) {
            return null;
        }

        int reqId = SurfConnectionManager.getInstance().newReqId();

        SurfMsgSysReq msg = new SurfMsgSysReq();

        msg.setReqId(reqId);
        msg.setReqType(SurfConstant.ReqType.SET_CONFIG);

        JsonArray array = new JsonArray();
        JsonObject status = new JsonObject();
        status.addProperty("type", "all");
        status.addProperty("period", 1000);
        array.add(status);

        JsonObject obj = new JsonObject();
        obj.add("status", array);

        msg.setData(obj);

        JsonMessage<SurfMsgSysReq> jsonMessage = new JsonMessage<>(SurfMsgSysReq.class);
        String jsonStr = jsonMessage.build(msg);

        logger.debug("Surf sys_req json: {}", jsonStr);

        return jsonStr;

    }

}
