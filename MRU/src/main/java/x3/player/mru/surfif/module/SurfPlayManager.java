package x3.player.mru.surfif.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SurfPlayManager {
    private static final Logger logger = LoggerFactory.getLogger(SurfPlayManager.class);

    private Map<String, SurfPlayInfo> playInfos;

    private int index;
    private static SurfPlayManager surfPlayManager = null;

    public static SurfPlayManager getInstance() {
        if (surfPlayManager == null) {
            surfPlayManager = new SurfPlayManager();
        }

        return surfPlayManager;
    }

    public SurfPlayManager() {
        playInfos = new HashMap<>();
        index = 0;
    }

    private int nextIndex() {
        synchronized (surfPlayManager) {
            index++;
            if (index == Short.MAX_VALUE) {
                index = 0;
            }
        }

        return index;
    }

    public String putData(String sessionId, int channel, String filename) {

        if (sessionId == null) {
            return null;
        }

        String playId = String.format("%s_%d", sessionId, nextIndex());
        SurfPlayInfo playInfo = new SurfPlayInfo(sessionId, channel, filename);

        playInfos.put(playId, playInfo);

        return playId;
    }

    public SurfPlayInfo getData(String playId) {
        if (playId == null) {
            return null;
        }

        SurfPlayInfo playInfo = null;
        if (playInfos.containsKey(playId)) {
            playInfo = playInfos.get(playId);
        }

        return playInfo;
    }

    public void removeData(String playId) {
        if (playInfos.containsKey(playId)) {
            playInfos.remove(playId);
        }

        logger.debug("Surf play data: remaining [{}]", playInfos.size());
    }

    public void updateData(String playId, boolean isPlaying) {
        if (playInfos.containsKey(playId)) {
            SurfPlayInfo playInfo = playInfos.get(playId);
            if (playInfo != null) {
                playInfo.setPlaying(isPlaying);
            }
        }
    }
}
