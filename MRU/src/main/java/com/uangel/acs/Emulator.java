package com.uangel.acs;

import com.uangel.acs.simulator.ScenarioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Emulator {
    private static final Logger logger = LoggerFactory.getLogger(Emulator.class);

    public static void main( String[] args )
    {
        logger.info("Emulator start");

        ScenarioManager scenarioManager = new ScenarioManager();
        scenarioManager.startScenario("/Users/lua/amf_scenario.txt");

        logger.warn("Emulator end");
    }
}
