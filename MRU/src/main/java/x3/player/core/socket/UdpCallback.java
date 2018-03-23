package x3.player.core.socket;

public interface UdpCallback {
    void onReceived(byte[] srcAddress, int srcPort, byte[] buf, int length);
}
