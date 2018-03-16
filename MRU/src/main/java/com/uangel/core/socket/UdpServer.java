package com.uangel.core.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpServer {

    private static final Logger logger = LoggerFactory.getLogger(UdpServer.class);

    private static final int MAX_BUFFER_SIZE = 4096;

    private DatagramSocket socket;
    private Thread thread = null;
    private byte[] buf = new byte[MAX_BUFFER_SIZE];

    public UdpServer(int port) {
        try {
            socket = new DatagramSocket(port);
            logger.debug("UdpServer ip {}", socket.getLocalAddress().toString());
            logger.debug("UdpServer ip {}", socket.getLocalSocketAddress().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private UdpCallback callback = null;

    public void setUdpCallback(UdpCallback callback) {
        this.callback = callback;
    }

    public boolean start() {
        if (thread != null) {
            return false;
        }

        thread = new Thread(new UdpServerRunnable());
        thread.start();

        return true;
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }

        socket.close();
    }

    class UdpServerRunnable implements Runnable {
        @Override
        public void run() {

            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                if (callback != null) {
                    callback.onReceived(packet.getAddress().getAddress(), packet.getPort(), buf, buf.length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
