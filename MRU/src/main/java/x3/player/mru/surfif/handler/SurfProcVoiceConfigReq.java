package x3.player.mru.surfif.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.surfif.messages.SurfMsgVoiceConfig;

public class SurfProcVoiceConfigReq {

    private static final Logger logger = LoggerFactory.getLogger(SurfProcVoiceConfigReq.class);

    private SurfMsgVoiceConfig msg;

    public SurfProcVoiceConfigReq() {
        msg = new SurfMsgVoiceConfig();
    }


}
