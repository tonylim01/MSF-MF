package x3.player.mru;

import x3.player.mru.config.AmfConfig;

public class AppInstance {

    private static AppInstance instance = null;
    private static final String CONFIG_FILE = "amf.conf";

    public static AppInstance getInstance() {
        if (instance == null) {
            instance = new AppInstance();
        }

        return instance;
    }

    private int instanceId = 0;
    private String configFile = null;
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

    public String getConfigFile() {
        return (configFile != null) ? configFile : CONFIG_FILE;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }
}
