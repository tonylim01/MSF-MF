package x3.player.mru.surfif.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.AppInstance;
import x3.player.mru.config.SurfConfig;
import x3.player.mru.surfif.messages.SurfMsgSetConfigStatus;
import x3.player.mru.surfif.messages.SurfMsgSysReq;
import x3.player.mru.surfif.messages.commands.SurfMsgGeneralCommandData;
import x3.player.mru.surfif.module.SurfConnectionManager;
import x3.player.mru.surfif.module.SurfJsonMessage;
import x3.player.mru.surfif.types.SurfConstant;

public class SurfProcCommand {
    private static final Logger logger = LoggerFactory.getLogger(SurfProcCommand.class);

    public String buildClearAll() {

        int reqId = SurfConnectionManager.getInstance().newReqId();

        SurfMsgSysReq msg = new SurfMsgSysReq();

        msg.setReqId(reqId);
        msg.setReqType(SurfConstant.REQ_TYPE_COMMAND);

        SurfMsgGeneralCommandData data = new SurfMsgGeneralCommandData();
        data.setCmdType(SurfConstant.CMD_TYPE_CLEAR_ALL_TOOLS);

        msg.setData(data);

        SurfJsonMessage<SurfMsgSysReq> jsonMessage = new SurfJsonMessage<>(SurfMsgSysReq.class);
        String jsonStr = jsonMessage.build(SurfMsgSysReq.MSG_NAME, msg);

        return jsonStr;

    }

}
