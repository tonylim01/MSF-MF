package x3.player.mru.surfif.module;

import x3.player.mru.surfif.handler.SurfProcToolReq;
import x3.player.mru.surfif.types.SurfConstant;

public class SurfChannelBuilder {
    SurfProcToolReq toolReq = null;

    public SurfChannelBuilder(int toolId) {
        toolReq = new SurfProcToolReq(toolId);
    }

    public void setToolType(String toolType) {
        toolReq.setToolType(toolType);
    }

    public void setRemove() {
        toolReq.setReqType(SurfConstant.REQ_TYPE_REMOVE);
    }

    public String build() {
        return toolReq.build();
    }

}
