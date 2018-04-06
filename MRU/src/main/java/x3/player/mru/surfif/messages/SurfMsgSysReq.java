package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;
import x3.player.mru.surfif.types.SurfConstant;

public class SurfMsgSysReq {

    public static final String MSG_NAME = "sys_req";

    @SerializedName("req_type")
    private String reqType;
    @SerializedName("req_id")
    private int reqId;
    private Object data;

    public String getReqType() {
        return this.reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    public int getReqId() {
        return this.reqId;
    }

    public void setReqId(int reqId) {
        this.reqId = reqId;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
