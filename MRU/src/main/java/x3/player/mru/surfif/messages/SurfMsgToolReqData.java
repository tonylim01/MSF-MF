package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

public class SurfMsgToolReqData {
    public static final String TOOL_TYPE_VOICE_P2P = "voice_p2p";
    public static final String TOOL_TYPE_VOICE_FE_IP = "voice_fe_ip";
    public static final String TOOL_TYPE_VOICE_MIXER = "voice_mixer";

    /**
     * Voice channel
     */
    @SerializedName("tool_type")
    private String toolType;
    @SerializedName("backend_tool_id")
    private Integer backendToolId;
    private SurfMsgVocoder decoder;
    private SurfMsgVocoder encoder;
    @SerializedName("RTP")
    private SurfMsgRtp rtp;
    @SerializedName("input_from_RTP")
    private Boolean inputFromRtp;

    /**
     * Mixer
     */
    @SerializedName("sampling_rate")
    private Integer samplingRate;
    @SerializedName("hangover_period")
    private Integer hangoverPeriod;
    @SerializedName("dominant_speakers")
    private Integer dominantSpeakers;

    public SurfMsgToolReqData() {
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

    public void newDecoder() {
        this.decoder = new SurfMsgVocoder();
    }

    public void setDecoder(SurfMsgVocoder decoder) {
        this.decoder = decoder;
    }

    public SurfMsgVocoder getEncoder() {
        return encoder;
    }

    public void newEncoder() {
        this.encoder = new SurfMsgVocoder();
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

    public void newRtp() {
        this.rtp = new SurfMsgRtp();
    }

    public Boolean getInputFromRtp() {
        return inputFromRtp;
    }

    public void setInputFromRtp(Boolean inputFromRtp) {
        this.inputFromRtp = inputFromRtp;
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }

    public int getHangoverPeriod() {
        return hangoverPeriod;
    }

    public void setHangoverPeriod(int hangoverPeriod) {
        this.hangoverPeriod = hangoverPeriod;
    }

    public int getDominantSpeakers() {
        return dominantSpeakers;
    }

    public void setDominantSpeakers(int dominantSpeakers) {
        this.dominantSpeakers = dominantSpeakers;
    }
}
