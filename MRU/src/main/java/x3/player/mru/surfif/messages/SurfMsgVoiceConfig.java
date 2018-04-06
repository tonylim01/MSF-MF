package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

public class SurfMsgVoiceConfig {

    @SerializedName("tool_req")
    private SurfMsgToolReq toolReq;

    public SurfMsgVoiceConfig() {
        toolReq = new SurfMsgToolReq();

    }

    public SurfMsgToolReq getToolReq() {
        return toolReq;
    }

    public void setToolReq(SurfMsgToolReq toolReq) {
        this.toolReq = toolReq;
    }
}
