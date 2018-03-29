package x3.player.mru.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericClass {
    private static final Logger logger = LoggerFactory.getLogger(GenericClass.class);

    protected void PrintOKFail(String fmtStr, boolean result) {
        String resultStr = StringUtil.getOkFail(result);
        if (result) {
            logger.info(fmtStr, resultStr);
        }
        else {
            logger.error(fmtStr, resultStr);
        }
    }
}
