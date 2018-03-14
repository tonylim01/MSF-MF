package com.uangel.acs.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public class NetUtil {

    private static final Logger logger = LoggerFactory.getLogger(NetUtil.class);

    public static boolean ping(String ip, int timeout) {

        boolean result = false;
        try {
//            InetAddress inet = InetAddress.getByAddress(new byte[] {(byte)172, (byte)16, (byte)246, (byte)130});
            InetAddress inet = InetAddress.getByName(ip);
            logger.debug("Sending ping to {}", inet);
            result = inet.isReachable(timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
