package x3.player.mru.rmqif.messages;

public class CommandStartReq {

    public static final String CMD_TYPE_MEDIA_PLAY = "media_play";

    private String type;
    private FileData data;

    public String getType() {
        return type;
    }

    public FileData getData() {
        return data;
    }
}
