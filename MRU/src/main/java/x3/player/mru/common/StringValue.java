package x3.player.mru.common;

public class StringValue {
    private static final String STR_OK = "OK";
    private static final String STR_FAIL = "FAIL";

    public static String getOkFail(boolean result) {
        return (result ? STR_OK : STR_FAIL);
    }
}
