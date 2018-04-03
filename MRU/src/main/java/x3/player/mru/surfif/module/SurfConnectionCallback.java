package x3.player.mru.surfif.module;

public interface SurfConnectionCallback {
    void onConnected();
    void onReady();
    void onSend(String data);
}
