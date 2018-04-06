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

    private int instanceId = 0;
    private AmfConfig config = null;

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public AmfConfig getConfig() {
        return config;
    }

    public void setConfig(AmfConfig config) {
        this.config = config;
    }
}
