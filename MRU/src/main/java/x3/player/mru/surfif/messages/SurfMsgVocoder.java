package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

public class SurfMsgVocoder {
    /**
     * Vocoder type
     */
    public static final String VOCODER_LINEAR   = "linear";
    public static final String VOCODER_ALAW     = "G.711alaw";
    public static final String VOCODER_ULAW     = "G.711ulaw";
    public static final String VOCODER_G729     = "G.729";
    public static final String VOCODER_G722     = "G.722";
    public static final String VOCODER_G7221    = "G.7221";
    public static final String VOCODER_G7231    = "G.7231";
    public static final String VOCODER_G726     = "G.726";
    public static final String VOCODER_AMR_NB   = "AMR_NB";
    public static final String VOCODER_AMR_WB   = "AMR_WB";
    public static final String VOCODER_G7111    = "G.711.1";
    public static final String VOCODER_EVS      = "EVS";
    public static final String VOCODER_CLEAR    = "clear";

    /**
     * Rate:
     * G.722    : 48, 56, 64 (Default: 64)
     * G.7221   : 16, 24, 32 (Default: 32)
     * G.7231   : 5.3, 6.3 (Default: 6.3)
     * G.726    : 16, 32 (Default: 32)
     */
    public static final String RATE_G722_48     = "48";
    public static final String RATE_G722_56     = "56";
    public static final String RATE_G722_64     = "64";
    public static final String RATE_G7221_16    = "16";
    public static final String RATE_G7221_24    = "24";
    public static final String RATE_G7221_32    = "32";
    public static final String RATE_G7231_53    = "5.3";
    public static final String RATE_G7231_63    = "6.3";
    public static final String RATE_G726_16     = "16";
    public static final String RATE_G726_32     = "32";

    /**
     * Packing:
     * AMR_NB, AMR_WB   : OA, BE (Default: OA)
     * G.726            : LE, BE (Default: LE)
     */
    public static final String PACKING_AMR_OA   = "OA";
    public static final String PACKING_AMR_BE   = "BE";
    public static final String PACKING_G726_LE  = "LE";
    public static final String PACKING_G726_BE  = "BE";

    /**
     * VAD type
     */
    public static final String SURF_VAD_TYPE_NONE = "none";
    public static final String SURF_VAD_TYPE_LIGHT = "light";
    public static final String SURF_VAD_TYPE_G729B = "G.729B";


    @SerializedName("type")
    private String vocoder;
    private String rate;
    private String packing;
    @SerializedName("packet_duration")
    private Integer packetDuration;
    @SerializedName("vad")
    private SurfVad vad;

    public String getVocoder() {
        return vocoder;
    }

    public void setVocoder(String vocoder) {
        this.vocoder = vocoder;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getPacking() {
        return packing;
    }

    public void setPacking(String packing) {
        this.packing = packing;
    }

    public int getPacketDuration() {
        return packetDuration;
    }

    public void setPacketDuration(int packetDuration) {
        if (packetDuration > 0) {
            this.packetDuration = packetDuration;
        }
    }

    public void setVad(boolean enabled, String type, boolean enableSid) {
        if (!enabled) {
            if (this.vad != null) {
                this.vad = null;
            }
        }
        else {
            if (this.vad == null) {
                this.vad = new SurfVad();
            }

            this.vad.setEnabled(enabled);
            if (type != null) {
                this.vad.setType(type);
            }
            if (enableSid) {
                this.vad.setEnableSid(enableSid);
            }
        }
    }

    private class SurfVad {
        private boolean enabled;
        private String type;
        @SerializedName("enable_SID")
        private Boolean enableSid;

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setEnableSid(boolean enableSid) {
            this.enableSid = enableSid;
        }
    }
}
