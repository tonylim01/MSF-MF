package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

public class SurfMsgConnect {
    @SerializedName("keep_alive_time")
    private int keeyAliveTime;

    @SerializedName("api_version")
    private int apiVersion[];

    public SurfMsgConnect() {
        apiVersion = new int[2];
    }

    public int getKeeyAliveTime() {
        return keeyAliveTime;
    }

    public void setKeeyAliveTime(int keeyAliveTime) {
        this.keeyAliveTime = keeyAliveTime;
    }

    public int getMajorVersion() {
        return apiVersion[0];
    }

    public int getMinorVersion() {
        return apiVersion[1];
    }

    public void setVersion(int majorVersion, int minorVersion) {
        this.apiVersion[0] = majorVersion;
        this.apiVersion[1] = minorVersion;
    }
}
