package x3.player.mru.surfif.handler;

import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.common.JsonMessage;
import x3.player.mru.surfif.messages.SurfMsgSysAns;
import x3.player.mru.surfif.messages.SurfMsgSysAnsData;

public class SurfProcSysAns {
    private static final Logger logger = LoggerFactory.getLogger(SurfProcSysAns.class);

    public SurfMsgSysAns parse(JsonElement element) {
        if (element == null) {
            return null;
        }

        SurfMsgSysAns msg = null;

        JsonMessage<SurfMsgSysAns> parser = new JsonMessage<>(SurfMsgSysAns.class);
        msg = parser.parse(element);

        if (msg == null) {
            return null;
        }

        logger.debug("SysAns reqId [{}] reqType [{}]", msg.getReqId(), msg.getReqType());

        if (msg.getData() != null) {
            parseResponseData(msg.getData());
        }

        return msg;
    }

    private boolean parseResponseData(SurfMsgSysAnsData data) {
        if (data == null) {
            return false;
        }

        logger.debug("\tResponse data: errCode [{}] desc [{}]", data.getErrorCode(), data.getDescription());
        return true;
    }
}
