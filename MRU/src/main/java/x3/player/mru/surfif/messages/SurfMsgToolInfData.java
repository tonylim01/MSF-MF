package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

public class SurfMsgToolInfData {
    @SerializedName("type")
    private String type;
    @SerializedName("file_name")
    private String filename;
    @SerializedName("app_info")
    private String appInfo;
    private String event;

    public String getType() {
        return type;
    }

    public String getFilename() {
        return filename;
    }

    public String getAppInfo() {
        return appInfo;
    }

    public String getEvent() {
        return event;
    }
}
