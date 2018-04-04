package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

public class SurfMsgSysAnsData {

    /**
     * Command/set_config response
     */
    @SerializedName("error_code")
    private int errorCode;
    private String description;

    public int getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }
}
