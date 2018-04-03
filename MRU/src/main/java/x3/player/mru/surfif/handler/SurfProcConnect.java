package x3.player.mru.surfif.handler;

import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.AppInstance;
import x3.player.mru.common.JsonMessage;
import x3.player.mru.config.SurfConfig;
import x3.player.mru.surfif.messages.SurfMsgConnect;

public class SurfProcConnect {
    private static final Logger logger = LoggerFactory.getLogger(SurfProcConnect.class);

    SurfMsgConnect msg = null;

    public SurfProcConnect() {
    }

    public SurfProcConnect(JsonElement element) {
        if (element != null) {
            initMessage(element);
        }
    }

    private void initMessage(JsonElement element) {
        if (element == null) {
            return;
        }

        JsonMessage<SurfMsgConnect> parser = new JsonMessage<>(SurfMsgConnect.class);
        msg = parser.parse(element);

        logger.debug("Parse connect: version {} {}", msg.getMajorVersion(), msg.getMinorVersion());
    }

    public String build() {

        SurfConfig config = AppInstance.getInstance().getConfig().getSurfConfig();

        if (config == null) {
            return null;
        }

        SurfMsgConnect msg = new SurfMsgConnect();

        msg.setVersion(config.getMajorVersion(), config.getMinorVersion());
        msg.setKeeyAliveTime(config.getKeepAliveTime());

        JsonMessage<SurfMsgConnect> jsonMessage = new JsonMessage<>(SurfMsgConnect.class);
        String jsonStr = jsonMessage.build(msg);

        logger.debug("Surf connect json: {}", jsonStr);

        return jsonStr;

    }
}
