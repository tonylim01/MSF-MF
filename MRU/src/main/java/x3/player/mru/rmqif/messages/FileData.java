package x3.player.mru.rmqif.messages;

import com.google.gson.annotations.SerializedName;

public class FileData {
    @SerializedName("media_type")
    private String mediaType;
    @SerializedName("play_file")
    private String playFile;
    @SerializedName("Def_volume")
    private int defVolume;
    @SerializedName("Mix_volume")
    private int mixVolume;
    @SerializedName("Play_Type")
    private String playType;    // "Caller_Only" or "both"

    public String getMediaType() {
        return mediaType;
    }

    public String getPlayFile() {
        return playFile;
    }

    public int getDefVolume() {
        return defVolume;
    }

    public int getMixVolume() {
        return mixVolume;
    }

    public String getPlayType() {
        return playType;
    }
}
