package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

public class SurfMsgToolData {
    public static final String TOOL_TYPE_VOICE_P2P = "voice_p2p";
    public static final String TOOL_TYPE_VOICE_FE_IP = "voice_fe_ip";

    @SerializedName("tool_type")
    private String toolType;
    @SerializedName("backend_tool_id")
    private int backendToolId;
    private SurfMsgVocoder decoder;
    private SurfMsgVocoder encoder;
    @SerializedName("RTP")
    private SurfMsgRtp rtp;

    public SurfMsgToolData() {
        this.decoder = new SurfMsgVocoder();
        this.encoder = new SurfMsgVocoder();
        this.rtp = new SurfMsgRtp();
    }

    public String getToolType() {

        return toolType;
    }

    public void setToolType(String toolType) {
        this.toolType = toolType;
    }

    public int getBackendToolId() {
        return backendToolId;
    }

    public void setBackendToolId(int backendToolId) {
        this.backendToolId = backendToolId;
    }

    public SurfMsgVocoder getDecoder() {
        return decoder;
    }

    public void setDecoder(SurfMsgVocoder decoder) {
        this.decoder = decoder;
    }

    public SurfMsgVocoder getEncoder() {
        return encoder;
    }

    public void setEncoder(SurfMsgVocoder encoder) {
        this.encoder = encoder;
    }

    public SurfMsgRtp getRtp() {
        return rtp;
    }

    public void setRtp(SurfMsgRtp rtp) {
        this.rtp = rtp;
    }
}
