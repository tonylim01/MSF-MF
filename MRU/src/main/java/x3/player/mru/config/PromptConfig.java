package x3.player.mru.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.core.config.DefaultConfig;
import x3.player.mru.common.NetUtil;
import x3.player.mru.common.StringUtil;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class PromptConfig extends DefaultConfig {

    private static final Logger logger = LoggerFactory.getLogger(PromptConfig.class);

    private static final float DEFAULT_VOLUME = 0.1f;

    private float mentVolume;
    private float bgmVolume;

    public PromptConfig(String configPath) {

        super(configPath);

        boolean result = load();
        logger.info("Load config [{}] ... [{}]", configPath, StringUtil.getOkFail(result));

        if (result == true) {
            loadConfig();
        }
    }

    @Override
    public String getStrValue(String session, String key, String defaultValue) {
        String value = super.getStrValue(session, key, defaultValue);

        logger.info("\tConfig key [{}] value [{}]", key, value);
        return value;
    }

    private void loadConfig() {
        try {
            mentVolume = getFloatValue("VOLUME", "MENT_VOLUME", DEFAULT_VOLUME);
            bgmVolume = getFloatValue("VOLUME", "BGM_VOLUME", DEFAULT_VOLUME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getMentVolume() {
        logger.debug("Ment volume [{}]", mentVolume);
        return mentVolume;
    }

    public float getBgmVolume() {
        logger.debug("BGM volume [{}]", bgmVolume);
        return bgmVolume;
    }
}
