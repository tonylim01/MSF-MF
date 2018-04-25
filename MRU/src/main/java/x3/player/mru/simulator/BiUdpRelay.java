package x3.player.mru.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.core.socket.UdpCallback;
import x3.player.core.socket.UdpSocket;
import x3.player.mru.rmqif.module.RmqClient;

import java.io.FileOutputStream;

public class BiUdpRelay {

    private static final Logger logger = LoggerFactory.getLogger(BiUdpRelay.class);

    /**
     * Working like below:
     *   srcLocalPort -> dstUdpSocket
     *   dstLocalPort -> srcUdpSocket
     */
    private UdpSocket srcUdpSocket = null;
    private UdpSocket dstUdpSocket = null;

    private int srcLocalPort;
    private int dstLocalPort;

    private String dstQueueName = null;

    public void setSrcLocalPort(int localPort) {
        srcLocalPort = localPort;
    }

    public void setDstLocalPort(int localPort) {
        dstLocalPort = localPort;
    }

    /**
     * Transports packets received on localPort to remoteIp:remotePort
     * @param remoteIpAddress
     * @param remotePort
     */
    public void openSrcUdpClient(String remoteIpAddress, int remotePort) {
        logger.debug("Open src UDP client. remote [{}:{}] lport [{}]", remoteIpAddress, remotePort, srcLocalPort);
        srcUdpSocket = new UdpSocket(remoteIpAddress, remotePort, srcLocalPort);
        srcUdpSocket.setUdpCallback(new RelayUdpCallback(srcUdpSocket));
        srcUdpSocket.start();
    }

    public void openDstUdpClient(String remoteIpAddress, int remotePort) {
        logger.debug("Open dst UDP client. remote [{}:{}] lport [{}]", remoteIpAddress, remotePort, dstLocalPort);
        dstUdpSocket = new UdpSocket(remoteIpAddress, remotePort, dstLocalPort);
        dstUdpSocket.setUdpCallback(new RelayUdpCallback(dstUdpSocket));
        dstUdpSocket.start();
    }

    public void setDupUdpQueue(String dstQueueName) {
        logger.debug("Open UDP relay queue [{}]", dstQueueName);
        this.dstQueueName = dstQueueName;
        if (srcUdpSocket != null) {
            srcUdpSocket.setRelayQueue(dstQueueName);
            srcUdpSocket.saveToFile("/tmp/aiif.pcm");
        }
    }

    public void closeUdpSocket() {
        if (srcUdpSocket != null) {
            srcUdpSocket.stop();
        }
        if (dstUdpSocket != null) {
            dstUdpSocket.stop();
        }
    }

    class RelayUdpCallback implements UdpCallback {

        private UdpSocket udpSocket;

        public RelayUdpCallback(UdpSocket udpSocket) {
            this.udpSocket = udpSocket;
        }

        @Override
        public void onReceived(byte[] srcAddress, int srcPort, byte[] buf, int length) {
            udpSocket.send(buf, length);
        }
    }
}
