package x3.player.mru.surfif.types;

public class SurfConstant {

    public static final String REQ_TYPE_SET_CONFIG = "set_config";
    public static final String REQ_TYPE_GET_CONFIG = "get_config";
    public static final String REQ_TYPE_COMMAND = "command";

    public static final String STR_INIT_MESSAGE = "surfapi";

    public static final String STR_CONNECT = "connect";

    /**
     * Command type strings
     */
    public static final String CMD_TYPE_CLEAR_ALL_TOOLS = "clear_all_tools";

    public enum ReqType {
        SET_CONFIG, GET_CONFIG
    }
}
