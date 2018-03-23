package x3.player.core.socket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpClient {

    private DatagramSocket socket;
    private InetAddress address;
    private int remotePort;

    public UdpClient(String ipAddress, int remotePort, int localPort) {
        try {
            socket = new DatagramSocket(localPort);
            address = InetAddress.getByName(ipAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.remotePort = remotePort;
    }

    public boolean send(byte[] buf) {
        if (buf == null || (buf != null && buf.length == 0)) {
            return false;
        }

        boolean result = false;
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, remotePort);
        try {
            socket.send(packet);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public void close() {
        if (socket != null) {
            socket.close();
        }
    }
}
