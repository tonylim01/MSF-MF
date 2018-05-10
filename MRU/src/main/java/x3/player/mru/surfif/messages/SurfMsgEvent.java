package x3.player.mru.surfif.messages;

public class SurfMsgEvent {
    public static final String EVENT_TYPE_ALL   = "all";

    private String type;
    private boolean enabled;

    public void setType(String type) {
        this.type = type;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
