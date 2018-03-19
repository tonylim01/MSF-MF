package com.uangel.acs.simulator;

import com.uangel.acs.AppInstance;
import com.uangel.acs.config.AmfConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpRelayManager {

    private static final Logger logger = LoggerFactory.getLogger(UdpRelayManager.class);

    private static final int DEFAULT_LOCAL_UDP_PORT_MIN = 20000;
    private static final int DEFAULT_LOCAL_UDP_PORT_MAX = 65535;

    private static UdpRelayManager udpRelayManager = null;

    public static UdpRelayManager getInstance() {
        if (udpRelayManager == null) {
            udpRelayManager = new UdpRelayManager();
        }
        return udpRelayManager;
    }

    private int localUdpPortMin;
    private int localUdpPortMax;
    private int currentUdpPort;

    public UdpRelayManager() {
        AmfConfig config = AppInstance.getInstance().getConfig();
        localUdpPortMin = config.getLocalUdpPortMin();
        localUdpPortMax = config.getLocalUdpPortMax();

        if (localUdpPortMin == 0) {
            localUdpPortMin = DEFAULT_LOCAL_UDP_PORT_MIN;
        }
        if (localUdpPortMax == 0) {
            localUdpPortMax = DEFAULT_LOCAL_UDP_PORT_MAX;
        }
        if (localUdpPortMin > localUdpPortMax) {
            localUdpPortMax = DEFAULT_LOCAL_UDP_PORT_MAX;
        }

        currentUdpPort = localUdpPortMin;
    }


    public int getNextLocalPort() {
        int result = currentUdpPort;
        currentUdpPort++;

        if (currentUdpPort > localUdpPortMax) {
            currentUdpPort = localUdpPortMin;
        }

        return result;
    }
}
