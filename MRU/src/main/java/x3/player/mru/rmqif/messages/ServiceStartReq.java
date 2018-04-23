package x3.player.mru.rmqif.messages;

import com.google.gson.annotations.SerializedName;

public class ServiceStartReq {
    @SerializedName("MDN")
    private String fromNo;
    @SerializedName("AIIF ID")
    private int aiifId;

    public String getFromNo() {
        return fromNo;
    }

    public int getAiifId() {
        return aiifId;
    }
}
