package x3.player.core.config;

import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;

public class DefaultConfig {

    private Ini ini;
    private String fileName;

    public DefaultConfig(String fileName) {
        this.fileName = fileName;
    }

    protected boolean load() {

        if (fileName == null) {
            return false;
        }

        boolean result = false;
        ini = new Ini();
        try {
            if (fileName.startsWith("/")) {
                ini.load(new File(fileName));
            }
            else {
                ini.load(this.getClass().getClassLoader().getResourceAsStream(fileName));
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public void close() {
        ini.clear();
    }

    public String getStrValue(String section, String key, String defaultValue) {
        if (section == null) {
            return defaultValue;
        }

        Profile.Section profileSection = ini.get(section);

        if (profileSection == null) {
            return defaultValue;
        }

        String value = profileSection.getOrDefault(key, defaultValue);

        if (value != null && value.contains("#")) {
            value = value.substring(0, value.indexOf('#')).trim();
        }

        return value;
    }

    public int getIntValue(String section, String key, int defaultValue) {

        int result;
        String configValue = getStrValue(section, key, null);
        if (configValue == null) {
            result = defaultValue;
        }
        else {
            try {
                result = Integer.valueOf(configValue);
            } catch (Exception e) {
                result = defaultValue;
            }
        }

        return result;
    }

    public float getFloatValue(String section, String key, float defaultValue) {

        float result;
        String configValue = getStrValue(section, key, null);
        if (configValue == null) {
            result = defaultValue;
        }
        else {
            try {
                result = Float.valueOf(configValue);
            } catch (Exception e) {
                result = defaultValue;
            }
        }

        return result;
    }
}
