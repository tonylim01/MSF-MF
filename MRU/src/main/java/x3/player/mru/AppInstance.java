package x3.player.mru;

import x3.player.mru.config.AmfConfig;

public class AppInstance {

    private static AppInstance instance = null;

    public static AppInstance getInstance() {
        if (instance == null) {
            instance = new AppInstance();
        }

        return instance;
    }

    private AmfConfig config = null;

    public AmfConfig getConfig() {
        return config;
    }

    public void setConfig(AmfConfig config) {
        this.config = config;
    }
}
