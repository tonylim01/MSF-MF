package x3.player.mru.surfif.module;

import java.util.Map;

public class SurfChannelGroup {

    /**
     * Map<TOOL_ID, SurfChannelInfo>
     */
    private Map<Integer, SurfChannelInfo> channels = null;

    private int maxSize;

    private int id;
    private boolean isBusy;

    public SurfChannelGroup(int size) {
        if (size <= 0) {
            return;
        }

        this.maxSize = size;

        for (int i = 0; i < size; i++) {
            SurfChannelInfo channelInfo = new SurfChannelInfo();
            channelInfo.setId(i);
            channels.put(i, channelInfo);
        }
    }

    public SurfChannelInfo getChannel(int toolId) {
        SurfChannelInfo channelInfo = null;
        if (toolId < maxSize) {
            channelInfo = channels.get(toolId);
        }

        return channelInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }
}
