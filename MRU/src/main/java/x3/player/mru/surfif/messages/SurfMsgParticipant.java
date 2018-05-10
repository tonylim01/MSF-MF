package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

public class SurfMsgParticipant {
    //"regular”, “listener”, “dominant”, “whisper" Default value: "regular"

    public static final String PAR_TYPE_REGULAR     = "regular";
    public static final String PAR_TYPE_LISTENER    = "listener";
    public static final String PAR_TYPE_DOMINANT    = "dominant";
    public static final String PAR_TYPE_WHISPER     = "whisper";

    public static final String PAR_ACTION_ADD       = "add";
    public static final String PAR_ACTION_REMOVE    = "remove";

    private Integer id;
    private String type;
    @SerializedName("tool_id")
    private Integer toolId;
    @SerializedName("whisper_to")
    private Integer whisperTo;
    private String action;

    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setToolId(int toolId) {
        this.toolId = toolId;
    }

    public void setWhisperTo(int whisperTo) {
        this.whisperTo = whisperTo;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
