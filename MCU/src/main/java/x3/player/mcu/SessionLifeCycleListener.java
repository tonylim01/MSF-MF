package x3.player.mcu;

/**
 * Created by hwaseob on 2018-02-27.
 */
public interface SessionLifeCycleListener {

    void sessionDeclined(McuSession s);
    void sessionCancelled(McuSession s);
    void sessionCreated(McuSession s);
    void sessionClosed(McuSession s);
    void sessionCannotCreate(McuSession s, Exception e);
}
