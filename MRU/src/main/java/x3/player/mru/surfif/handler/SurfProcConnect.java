package x3.player.mru.surfif.handler;

import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.AppInstance;
import x3.player.mru.common.JsonMessage;
import x3.player.mru.config.SurfConfig;
import x3.player.mru.surfif.messages.SurfMsgConnect;
import x3.player.mru.surfif.module.SurfJsonMessage;

public class SurfProcConnect {
    private static final Logger logger = LoggerFactory.getLogger(SurfProcConnect.class);

    public SurfProcConnect() {
    }

    public SurfMsgConnect parse(JsonElement element) {
        if (element == null) {
            return null;
        }

        JsonMessage<SurfMsgConnect> parser = new JsonMessage<>(SurfMsgConnect.class);
        SurfMsgConnect msg = parser.parse(element);

        logger.debug("Parse connect: version {} {}", msg.getMajorVersion(), msg.getMinorVersion());

        return msg;
    }

    public String build() {

        SurfConfig config = AppInstance.getInstance().getConfig().getSurfConfig();

        if (config == null) {
            return null;
        }

        SurfMsgConnect msg = new SurfMsgConnect();

        msg.setVersion(config.getMajorVersion(), config.getMinorVersion());
        msg.setKeeyAliveTime(config.getKeepAliveTime());

        SurfJsonMessage<SurfMsgConnect> jsonMessage = new SurfJsonMessage<>(SurfMsgConnect.class);
        String jsonStr = jsonMessage.build(SurfMsgConnect.MSG_NAME, msg);

        logger.debug("Surf connect json: {}", jsonStr);

        return jsonStr;

    }
}
