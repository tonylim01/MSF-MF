package x3.player.mru;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.service.ServiceManager;

public class App
{
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args )
    {
        ServiceManager serviceManager = new ServiceManager();
        serviceManager.loop();
    }
}
