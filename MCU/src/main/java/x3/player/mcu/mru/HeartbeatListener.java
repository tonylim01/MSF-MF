package x3.player.mcu.mru;

import java.util.Map;

/**
 * Created by hwaseob on 2018-03-06.
 */
public interface HeartbeatListener {
    void beat(Map<String,Object> h);
}
