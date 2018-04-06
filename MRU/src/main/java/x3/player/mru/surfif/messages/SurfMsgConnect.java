package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

public class SurfMsgConnect {

    public static final String MSG_NAME = "connect";

    @SerializedName("keep_alive_timeout")
    private int keeyAliveTime;

    @SerializedName("api_version")
    private int apiVersion[];

    public SurfMsgConnect() {
        apiVersion = new int[2];
    }

    public int getKeeyAliveTime() {
        return this.keeyAliveTime;
    }

    public void setKeeyAliveTime(int keeyAliveTime) {
        this.keeyAliveTime = keeyAliveTime;
    }

    public int getMajorVersion() {
        return this.apiVersion[0];
    }

    public int getMinorVersion() {
        return this.apiVersion[1];
    }

    public void setVersion(int majorVersion, int minorVersion) {
        this.apiVersion[0] = majorVersion;
        this.apiVersion[1] = minorVersion;
    }
}
