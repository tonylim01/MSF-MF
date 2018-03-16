package com.uangel.acs.simulator;

import com.uangel.core.socket.UdpServer;

public class UdpRelay {

    private UdpServer udpServer = null;

    public UdpRelay() {
    }

    public void openUdpServer(int serverPort) {
        udpServer = new UdpServer(serverPort);
    }

    public void closeUdpServer() {
        udpServer.stop();
    }

    public void openUdpClient() {

    }

    public void closeUdpClient() {

    }
}
