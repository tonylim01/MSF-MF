package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

public class SurfMsgSysInf {

    public static final String MSG_NAME = "sys_inf";

    @SerializedName("inf_type")
    private String infType;
    private SurfMsgSysInfData data;

    public String getInfType() {
        return infType;
    }

    public void setInfType(String infType) {
        this.infType = infType;
    }

    public SurfMsgSysInfData getData() {
        return data;
    }

    public void setData(SurfMsgSysInfData data) {
        this.data = data;
    }
}
