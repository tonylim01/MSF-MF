package x3.player.mcu;

/**
 * Created by hwaseob on 2018-02-27.
 */
public class NullSessionLifeCycleListener implements SessionLifeCycleListener {
    public void sessionDeclined(Session s) {
    }

    public void sessionCancelled(Session s) {
    }

    public void sessionCreated(Session s) {
    }

    public void sessionClosed(Session s) {
    }

    @Override
    public void sessionCannotCreate(Session s, Exception e) {
    }
}
