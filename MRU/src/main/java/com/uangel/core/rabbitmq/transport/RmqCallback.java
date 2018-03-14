package com.uangel.core.rabbitmq.transport;

public interface RmqCallback {
    void onReceived(String msg);
}
