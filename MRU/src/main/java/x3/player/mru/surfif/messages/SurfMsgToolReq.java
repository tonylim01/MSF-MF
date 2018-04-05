package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

public class SurfMsgToolReq {

    public static final String MSG_NAME = "tool_req";
    
    @SerializedName("tool_id")
    private int toolId;
    @SerializedName("req_id")
    private int reqId;
    @SerializedName("req_type")
    private String reqType;
    @SerializedName("data")
    private SurfMsgToolReqData data;

    public SurfMsgToolReq() {
        this.data = new SurfMsgToolReqData();
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

    public SurfMsgToolReqData getData() {
        return data;
    }

    public void setData(SurfMsgToolReqData data) {
        this.data = data;
    }
}
