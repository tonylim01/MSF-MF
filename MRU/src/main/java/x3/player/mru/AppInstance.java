package x3.player.mru;

import x3.player.mru.config.AmfConfig;
import x3.player.mru.config.PromptConfig;

public class AppInstance {

    private static AppInstance instance = null;
    private static final String AMF_CONFIG_FILE = "amf.conf";

    public static AppInstance getInstance() {
        if (instance == null) {
            instance = new AppInstance();
        }

        return instance;
    }

    private int instanceId = 0;
    private String configFile = null;
    private AmfConfig amfConfig = null;
    //private PromptConfig promptConfig = null;

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public AmfConfig getConfig() {
        return amfConfig;
    }

    public void setConfig(AmfConfig config) {
        this.amfConfig = config;
        
//        if (config != null && config.getPromptConfPath() != null) {
//            promptConfig = new PromptConfig(config.getPromptConfPath());
//        }
    }

    public String getConfigFile() {
        return (configFile != null) ? configFile : AMF_CONFIG_FILE;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public PromptConfig getPromptConfig() {
        PromptConfig promptConfig = null;
        if (amfConfig != null && amfConfig.getPromptConfPath() != null) {
            promptConfig = new PromptConfig(amfConfig.getPromptConfPath());
        }
        return promptConfig;
    }
}
