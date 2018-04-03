package x3.player.mru.surfif.messages;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import x3.player.mru.surfif.types.SurfConstant;

public class SurfMsgSysReq {

    @SerializedName("sys_req")
    private SysReq sysReq;

    public SurfMsgSysReq() {
        sysReq = new SysReq();
    }

    public String getReqType() {
        return sysReq.reqType;
    }

    public void setReqType(String reqType) {
        sysReq.reqType = reqType;
    }

    public void setReqType(SurfConstant.ReqType reqType) {
        sysReq.reqType = (reqType == SurfConstant.ReqType.SET_CONFIG) ?
                SurfConstant.REQ_TYPE_SET_CONFIG : SurfConstant.REQ_TYPE_GET_CONFIG;
    }

    public int getReqId() {
        return sysReq.reqId;
    }

    public void setReqId(int reqId) {
        sysReq.reqId = reqId;
    }

    public JsonObject getData() {
        return sysReq.data;
    }

    public void setData(JsonObject data) {
        sysReq.data = data;
    }

    class SysReq {
        @SerializedName("req_type")
        private String reqType;
        @SerializedName("req_id")
        private int reqId;
        private JsonObject data;
    }
}
