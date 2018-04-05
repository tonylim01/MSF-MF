package x3.player.mru;

import com.sun.deploy.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.common.NetUtil;
import x3.player.mru.common.StringUtil;
import x3.player.mru.service.ServiceManager;

public class App
{
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args )
    {
        int instanceId = 0;

        if (args != null && args.length > 0 && StringUtil.isNumeric(args[0])) {
            instanceId = Integer.valueOf(args[0]);
        }

        logger.info("MRUD [{}] start", instanceId);

        AppInstance.getInstance().setInstanceId(instanceId);

        ServiceManager serviceManager = ServiceManager.getInstance();
        serviceManager.loop();
    }
}
