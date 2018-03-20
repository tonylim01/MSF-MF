package x3.player.core.rabbitmq.transport;

public interface RmqCallback {
    void onReceived(String msg);
}
