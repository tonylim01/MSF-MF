package x3.player.mru.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.core.socket.UdpCallback;
import x3.player.core.socket.UdpSocket;

public class RtpRelay {

    private static final Logger logger = LoggerFactory.getLogger(RtpRelay.class);

    /**
     * udpSocket : RTP -> RTP relay
     * tabSocket : RTP -> UDP (Packet) relay
     */
    private UdpSocket udpSocket;
    private UdpSocket tabsocket;    // Or RmqClient

    private int localPort;

    public RtpRelay(int localPort) {
        this.localPort = localPort;
    }

    public void setRtpDestination(String remoteIpAddress, int remotePort) {
        // TODO
    }

    public void setTabDestination(String remoteIpAddress, int remotePort) {
        // TODO
    }

    public void openRtpClient(String remoteIpAddress, int remotePort) {
        logger.debug("Open UDP client. remote [{}:{}] lport [{}]", remoteIpAddress, remotePort, localPort);
        udpSocket = new UdpSocket(remoteIpAddress, remotePort, localPort);
        udpSocket.setUdpCallback(new RtpRelay.RelayUdpCallback());
        udpSocket.start();
    }

    public void closeRtpClient() {
        if (udpSocket != null) {
            udpSocket.stop();
        }
    }

    class RelayUdpCallback implements UdpCallback {
        @Override
        public void onReceived(byte[] srcAddress, int srcPort, byte[] buf, int length) {
            logger.debug("UDP received: size [{}]", length);
            udpSocket.send(buf, length);
        }
    }
}
