package x3.player.mru.rmqif.messages;

import com.google.gson.annotations.SerializedName;

public class ServiceStartReq {
    @SerializedName("from_no")
    private String fromNo;
    @SerializedName("aiif_id")
    private int aiifId;

    public String getFromNo() {
        return fromNo;
    }

    public int getAiifId() {
        return aiifId;
    }
}
