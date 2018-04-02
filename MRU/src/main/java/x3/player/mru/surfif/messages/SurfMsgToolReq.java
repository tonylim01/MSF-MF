package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

public class SurfMsgToolReq {
    public static final String REQ_TYPE_SET_CONFIG = "set_config";
    public static final String REQ_TYPE_GET_CONFIG = "get_config";

    @SerializedName("tool_id")
    private int toolId;
    @SerializedName("req_id")
    private int reqId;
    @SerializedName("req_type")
    private String reqType;
    @SerializedName("data")
    private SurfMsgToolData data;

    public SurfMsgToolReq() {
        this.data = new SurfMsgToolData();
    }

    public int getToolId() {
        return toolId;
    }

    public void setToolId(int toolId) {
        this.toolId = toolId;
    }

    public int getReqId() {
        return reqId;
    }

    public void setReqId(int reqId) {
        this.reqId = reqId;
    }

    public String getReqType() {
        return reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    public SurfMsgToolData getData() {
        return data;
    }

    public void setData(SurfMsgToolData data) {
        this.data = data;
    }
}
