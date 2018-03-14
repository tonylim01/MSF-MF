package com.uangel.core.config;

import java.util.Properties;

public class DefaultConfig {

    private Properties configs;
    private String fileName;

    public DefaultConfig(String fileName) {
        this.fileName = fileName;
    }

    protected boolean load() {

        boolean result = false;
        configs = new Properties();
        try {
            configs.load(this.getClass().getClassLoader().getResourceAsStream(fileName));
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public void close() {
        configs.clear();
    }

    public String getStrValue(String key, String defaultValue) {
        return configs.getProperty(key, defaultValue);
    }

    public int getIntValue(String key, int defaultValue) {

        int result;
        String value = configs.getProperty(key, null);

        if (value == null) {
            result = defaultValue;
        }
        else {
            try {
                result = Integer.valueOf(value);
            } catch (Exception e) {
                result = defaultValue;
            }
        }

        return result;
    }
}
