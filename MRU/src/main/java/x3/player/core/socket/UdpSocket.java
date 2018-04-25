package x3.player.core.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.rmqif.module.RmqClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpSocket {

    private static final Logger logger = LoggerFactory.getLogger(UdpServer.class);

    private static final int MAX_BUFFER_SIZE = 4096;
    private static final int RTP_HEADER_SIZE = 12;

    private DatagramSocket socket;
    private Thread thread = null;
    private byte[] buf = new byte[MAX_BUFFER_SIZE];
    private boolean isQuit = false;
    private InetAddress address;
    private int localPort;
    private int remotePort;

    private RmqClient rmqClient = null;
    private FileOutputStream fileStream = null;

    public UdpSocket(String ipAddress, int remotePort, int localPort) {
        try {
            socket = new DatagramSocket(localPort);
            address = InetAddress.getByName(ipAddress);
            logger.debug("UdpSocket ip {} port {}", socket.getLocalSocketAddress().toString(), localPort);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.remotePort = remotePort;
        this.localPort = localPort;
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
        if (fileStream != null) {
            try {
                fileStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (thread != null) {
            thread.interrupt();
            thread = null;
        }

        socket.close();
    }

    public boolean send(byte[] buf, int size) {
        if (buf == null || (buf != null && buf.length == 0)) {
            return false;
        }

        if (size > buf.length) {
            return false;
        }

//        logger.debug("Remote port {} size {}", remotePort, size);
        boolean result = false;
        DatagramPacket packet = new DatagramPacket(buf, size, address, remotePort);
        try {
            socket.send(packet);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (rmqClient != null && rmqClient.isConnected() &&
                buf != null && size > 0) {

            rmqClient.send(buf, size);

            if (fileStream != null) {
                try {
                    fileStream.write(buf, RTP_HEADER_SIZE, size - RTP_HEADER_SIZE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public void setRelayQueue(String queueName) {
        rmqClient = RmqClient.getInstance(queueName);
    }

    public void saveToFile(String filename) {
        try {
            fileStream = new FileOutputStream(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class UdpServerRunnable implements Runnable {
        @Override
        public void run() {

            logger.info("UdpSocket server ({}) start", localPort);
            while (!isQuit) {
                try {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    if (callback != null) {
                        callback.onReceived(packet.getAddress().getAddress(), packet.getPort(), buf, packet.getLength());
                    }
                } catch (Exception e) {
                    logger.warn("Exception [{}] [{}]", e.getClass(), e.getMessage());
                    if (e.getClass() != IOException.class) {
                        isQuit = true;
                    }
                }
            }
            logger.info("UdpServer server ({}) end", localPort);
        }
    }
}
