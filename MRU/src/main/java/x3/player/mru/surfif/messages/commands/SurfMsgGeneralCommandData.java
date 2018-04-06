package x3.player.mru.surfif.messages.commands;

import com.google.gson.annotations.SerializedName;

public class SurfMsgGeneralCommandData {
    @SerializedName("cmd_type")
    private String cmdType;

    public String getCmdType() {
        return cmdType;
    }

    public void setCmdType(String cmdType) {
        this.cmdType = cmdType;
    }
}
