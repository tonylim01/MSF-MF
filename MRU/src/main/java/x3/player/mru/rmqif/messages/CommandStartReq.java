package x3.player.mru.rmqif.messages;

public class CommandStartReq {

    public static final String CMD_TYPE_MEDIA_PLAY = "media_play";

    private String type;
    private int channel;    // 1: BGM, 2: ment
    private FileData data;

    public String getType() {
        return type;
    }

    public int getChannel() {
        return channel;
    }

    public FileData getData() {
        return data;
    }
}
