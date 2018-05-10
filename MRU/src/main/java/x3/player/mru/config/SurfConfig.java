package x3.player.mru.config;

import x3.player.mru.surfif.messages.SurfMsgVocoder;

public class SurfConfig {

    public static final int DEFAULT_INTERNAL_SAMPLE_RATE = 8000;
    public static final String DEFAULT_INTERNAL_CODEC = SurfMsgVocoder.VOCODER_LINEAR;

    private int majorVersion;
    private int minorVersion;
    private int keepAliveTime;

    private String surfIp;
    private int surfPort;

    private int totalChannels;

    private String internalCodec;
    private int internalPayload;
    private int internalSampleRate;

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public String getSurfIp() {
        return surfIp;
    }

    public void setSurfIp(String surfIp) {
        this.surfIp = surfIp;
    }

    public int getSurfPort() {
        return surfPort;
    }

    public void setSurfPort(int surfPort) {
        this.surfPort = surfPort;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getTotalChannels() {
        return totalChannels;
    }

    public void setTotalChannels(int totalChannels) {
        this.totalChannels = totalChannels;
    }

    public String getInternalCodec() {
        return internalCodec;
    }

    public void setInternalCodec(String internalCodec) {
        this.internalCodec = internalCodec;
    }

    public int getInternalPayload() {
        return internalPayload;
    }

    public void setInternalPayload(int internalPayload) {
        this.internalPayload = internalPayload;
    }

    public int getInternalSampleRate() {
        return internalSampleRate;
    }

    public void setInternalSampleRate(int internalSampleRate) {
        this.internalSampleRate = internalSampleRate;
    }
}
