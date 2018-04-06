package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

public class SurfMsgSysAns {
    public static final String MSG_NAME = "sys_ans";

    @SerializedName("req_id")
    private int reqId;
    @SerializedName("req_type")
    private String reqType;
    private SurfMsgSysAnsData data;

    public int getReqId() {
        return reqId;
    }

    public String getReqType() {
        return reqType;
    }

    public SurfMsgSysAnsData getData() {
        return data;
    }
}
