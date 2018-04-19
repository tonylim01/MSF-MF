package x3.player.mcu;

/**
 * Created by hwaseob on 2018-02-27.
 */
public interface SessionLifeCycleListener {

    void sessionDeclined(Session s);
    void sessionCancelled(Session s);
    void sessionCreated(Session s);
    void sessionClosed(Session s);
    void sessionCannotCreate(Session s, Exception e);
}
