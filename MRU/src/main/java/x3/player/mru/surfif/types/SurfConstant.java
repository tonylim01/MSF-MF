package x3.player.mru.surfif.types;

public class SurfConstant {

    /**
     * Req type strings
     */
    public static final String REQ_TYPE_SET_CONFIG = "set_config";
    public static final String REQ_TYPE_GET_CONFIG = "get_config";
    public static final String REQ_TYPE_COMMAND = "command";

    public static final String STR_INIT_MESSAGE = "surfapi";

    public static final String STR_CONNECT = "connect";

    /**
     * Tool type strings
     */
    public static final String TOOL_TYPE_VOICE_P2P = "voice_p2p";
    public static final String TOOL_TYPE_VOICE_FE_IP = "voice_fe_ip";
    public static final String TOOL_TYPE_VOICE_MIXER = "voice_mixer";
    public static final String TOOL_TYPE_FILE_READER = "file_reader";

    /**
     * Command type strings
     */
    public static final String CMD_TYPE_CLEAR_ALL_TOOLS = "clear_all_tools";
    // Append files to the playlist
    public static final String CMD_TYPE_PLAY_LIST_APPEND = "play_list_append";
    // Stops and clears a playlist
    public static final String CMD_TYPE_PLAY_LIST_CLEAR = "play_list_clear";
    // Starts playing files from the playlist
    public static final String CMD_TYPE_PLAY = "play";
    // Pauses playing files from the playlist
    public static final String CMD_TYPE_PAUSE = "pause";

    public enum ReqType {
        SET_CONFIG, GET_CONFIG
    }
}
