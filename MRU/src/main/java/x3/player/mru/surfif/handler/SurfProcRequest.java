package x3.player.mru.surfif.handler;

import x3.player.mru.surfif.module.SurfConnectionManager;

public abstract class SurfProcRequest {
    protected int getReqId() {
        return SurfConnectionManager.getInstance().newReqId();
    }
}
