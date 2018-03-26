package x3.player.mru.rmqif.messages;

import com.google.gson.annotations.SerializedName;

public class ServiceStartReq {
    @SerializedName("from_no")
    private String fromNo;
    @SerializedName("to_no")
    private String toNo;

    public void setFromNo(String fromNo) {
        this.fromNo = fromNo;
    }

    public void setToNo(String toNo) {
        this.toNo = toNo;
    }
}
