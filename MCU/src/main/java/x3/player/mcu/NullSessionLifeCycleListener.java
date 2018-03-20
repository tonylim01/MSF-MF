package x3.player.mcu;

/**
 * Created by hwaseob on 2018-02-27.
 */
public class NullSessionLifeCycleListener implements SessionLifeCycleListener {
    public void sessionDeclined(McuSession s) {
    }

    public void sessionCancelled(McuSession s) {
    }

    public void sessionCreated(McuSession s) {
    }

    public void sessionClosed(McuSession s) {
    }

    @Override
    public void sessionCannotCreate(McuSession s, Exception e) {
    }
}
