package x3.player.mru;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.common.StringUtil;
import x3.player.mru.service.ServiceManager;

public class App
{
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args )
    {
        int instanceId = 0;
        String configPath = null;

        if (args != null && args.length > 0) {
            if (StringUtil.isNumeric(args[0])) {
                instanceId = Integer.valueOf(args[0]);
            }

            if (args.length > 1) {
                configPath = args[1];
            }
        }

        logger.info("MRUD [{}] start", instanceId);

        AppInstance.getInstance().setInstanceId(instanceId);
        if (configPath != null) {
            AppInstance.getInstance().setConfigFile(configPath);
        }

        ServiceManager serviceManager = ServiceManager.getInstance();
        serviceManager.loop();
    }
}
