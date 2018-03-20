package com.uangel.core.socket;

public interface UdpCallback {
    void onReceived(byte[] srcAddress, int srcPort, byte[] buf, int length);
}
