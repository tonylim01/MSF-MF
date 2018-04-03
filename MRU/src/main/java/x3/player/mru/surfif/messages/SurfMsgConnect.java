package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

public class SurfMsgConnect {

    private Connect connect;

    class Connect {
        @SerializedName("keep_alive_timeout")
        private int keeyAliveTime;

        @SerializedName("api_version")
        private int apiVersion[];

        public Connect() {
            apiVersion = new int[2];
        }
    }

    public SurfMsgConnect() {

        connect = new Connect();
    }

    public int getKeeyAliveTime() {
        return connect.keeyAliveTime;
    }

    public void setKeeyAliveTime(int keeyAliveTime) {
        connect.keeyAliveTime = keeyAliveTime;
    }

    public int getMajorVersion() {
        return connect.apiVersion[0];
    }

    public int getMinorVersion() {
        return connect.apiVersion[1];
    }

    public void setVersion(int majorVersion, int minorVersion) {
        connect.apiVersion[0] = majorVersion;
        connect.apiVersion[1] = minorVersion;
    }
}
