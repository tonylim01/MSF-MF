package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

public class SurfMsgToolInf {

    public static final String MSG_NAME = "tool_inf";

    @SerializedName("inf_type")
    private String infType;
    @SerializedName("tool_id")
    private int toolId;
    @SerializedName("app_info")
    private String appInfo;
    @SerializedName("tool_type")
    private String toolType;

    private SurfMsgToolInfData data;

    public String getInfType() {
        return infType;
    }

    public int getToolId() {
        return toolId;
    }

    public String getAppInfo() {
        return appInfo;
    }

    public String getToolType() {
        return toolType;
    }

    public SurfMsgToolInfData getData() {
        return data;
    }
}
