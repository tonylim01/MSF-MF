package com.uangel.acs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App
{
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args )
    {
        ServiceManager serviceManager = new ServiceManager();
        serviceManager.loop();
    }
}
