package x3.player.mru.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SurfManager {
    private static final Logger logger = LoggerFactory.getLogger(SurfManager.class);

    private static SurfManager surfManager = null;

    public static SurfManager getInstance() {
        if (surfManager == null) {
            surfManager = new SurfManager();
        }

        return surfManager;
    }
}
