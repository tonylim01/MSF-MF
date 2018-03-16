package com.uangel.acs.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetUtil {

    private static final Logger logger = LoggerFactory.getLogger(NetUtil.class);

    public static boolean ping(String ip, int timeout) {

        boolean result = false;
        try {
            InetAddress inet = InetAddress.getByName(ip);
            logger.debug("Sending ping to {}", inet);
            result = inet.isReachable(timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String getLocalIP() {
        String ipAddress = null;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while ((networkInterfaces.hasMoreElements())) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while ((inetAddresses.hasMoreElements())) {

                    InetAddress inetAddress = inetAddresses.nextElement();
                    String hostAddress = inetAddress.getHostAddress();
                    logger.debug("Network address [{}]", hostAddress);

                    if (ipAddress == null) {
                        ipAddress = hostAddress;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ipAddress;
    }
}
