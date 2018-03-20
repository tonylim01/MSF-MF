package com.uangel.acs.simulator;

import com.uangel.core.socket.UdpCallback;
import com.uangel.core.socket.UdpClient;
import com.uangel.core.socket.UdpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpRelay {

    private static final Logger logger = LoggerFactory.getLogger(UdpRelay.class);

    private UdpServer udpServer = null;
    private UdpClient udpClient = null;

    public UdpRelay() {
    }

    public void openUdpServer(int serverPort) {
        udpServer = new UdpServer(serverPort);
        udpServer.setUdpCallback(new RelayUdpCallback());
        udpServer.start();
    }

    public void closeUdpServer() {
        udpServer.stop();
    }

    public void openUdpClient(String remoteIpAddress, int remotePort, int localPort) {
        udpClient = new UdpClient(remoteIpAddress, remotePort, localPort);
    }

    public void closeUdpClient() {
        if (udpClient != null) {
            udpClient.close();
        }
    }

    class RelayUdpCallback implements UdpCallback {
        @Override
        public void onReceived(byte[] srcAddress, int srcPort, byte[] buf, int length) {
            logger.debug("UDP received: size [{}]", length);
            udpClient.send(buf);
        }
    }

}
