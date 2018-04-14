package x3.player.mru.surfif.module;

import x3.player.mru.surfif.handler.SurfProcToolReq;
import x3.player.mru.surfif.types.SurfConstant;

public class SurfPlayBuilder {

    SurfProcToolReq toolReq = null;

    public SurfPlayBuilder(int toolId) {
        toolReq = new SurfProcToolReq(toolId);
    }

    public void setFileReader(int audioDstToolId) {
        toolReq.setToolType(SurfConstant.TOOL_TYPE_FILE_READER);
        toolReq.setAudioEnabled(true);
        toolReq.setAudioDstToolId(audioDstToolId);
    }

    public void setPlayListAppend(String filename) {
        toolReq.setCmdType(SurfConstant.CMD_TYPE_PLAY_LIST_APPEND);
        toolReq.addFile(filename, 0, null, 0);
    }

    public void setPlayListClear() {
        toolReq.setCmdType(SurfConstant.CMD_TYPE_PLAY_LIST_CLEAR);
    }

    public void setPlayStart() {
        toolReq.setCmdType(SurfConstant.CMD_TYPE_PLAY);
    }

    public void setPlayPause() {
        toolReq.setCmdType(SurfConstant.CMD_TYPE_PAUSE);
    }

    public String build() {
        return toolReq.build();
    }
}
