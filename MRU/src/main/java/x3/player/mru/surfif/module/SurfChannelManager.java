package x3.player.mru.surfif.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.AppInstance;
import x3.player.mru.surfif.handler.SurfProcToolReq;
import x3.player.mru.surfif.messages.*;
import x3.player.mru.surfif.types.SurfConstant;
import x3.player.mru.surfif.types.SurfEndpointType;

public class SurfChannelManager {
    private static final Logger logger = LoggerFactory.getLogger(SurfChannelManager.class);

    private static final int DEFAULT_GROUP_SIZE = 2;
    private static final int DEFAULT_CHANNEL_COUNT = 10;

    private static final int CHANNELS_PER_GROUP = 9;
    private static final int BASE_UDP_PORT = 10000;

    public static final int TOOL_ID_MIXER   = 0;
    public static final int TOOL_ID_CG_RX   = 1;
    public static final int TOOL_ID_CG_TX   = 2;
    public static final int TOOL_ID_PAR_CG  = 3;
    public static final int TOOL_ID_CD      = 4;
    public static final int TOOL_ID_PLAY    = 5;
    public static final int TOOL_ID_BG      = 6;
    public static final int TOOL_ID_PAR_PLAY    = 7;
    public static final int TOOL_ID_PAR_BG  = 8;

    private static SurfChannelManager surfChannelManager = null;

    public static SurfChannelManager getInstance() {
        if (surfChannelManager == null) {
            int totalChannels = AppInstance.getInstance().getConfig().getSurfConfig().getTotalChannels();
            if (totalChannels <= 0) {
                totalChannels = DEFAULT_CHANNEL_COUNT;
            }

            surfChannelManager = new SurfChannelManager(totalChannels);
        }

        return surfChannelManager;
    }

    private int totalChannels;
    private int groupCount;

    private SurfChannelGroup channelGroups[] = null;
    private int lastGroupId = -1;

    /***
     * Initializes local variables
     * @param totalChannels
     */
    public SurfChannelManager(int totalChannels) {
        this.totalChannels = totalChannels;
        this.groupCount = totalChannels / CHANNELS_PER_GROUP;

        if (groupCount <= 0) {
            groupCount = DEFAULT_GROUP_SIZE;
        }

        channelGroups = new SurfChannelGroup[groupCount];

        for (int i = 0; i < groupCount; i++) {
            channelGroups[i] = new SurfChannelGroup(CHANNELS_PER_GROUP);
            channelGroups[i].setId(i);
        }
    }

    /**
     * Finds and returns an idle resource
     * @return
     */
    public int getIdleChannelGroup() {
        if (channelGroups == null) {
            return -1;
        }

        int groupId = -1;
        synchronized (channelGroups) {
            lastGroupId++;
            if (lastGroupId >= groupCount) {
                lastGroupId = 0;
            }

            for (int i = lastGroupId; i < groupCount; i++) {
                if (!channelGroups[i].isBusy()) {
                    groupId = channelGroups[i].getId();
                    break;
                }
            }

            if (groupId < 0 && lastGroupId > 0) {
                for (int i = 0; i < lastGroupId; i++) {
                    if (!channelGroups[i].isBusy()) {
                        groupId = channelGroups[i].getId();
                        break;
                    }
                }
            }
        }

        return groupId;
    }

    /**
     * Calculates a real tool id allocated in the dsp
     * @param groupId
     * @return
     */
    public static int getReqToolId(int groupId, int toolId) {
        return groupId * CHANNELS_PER_GROUP + toolId;
    }

    /**
     * Returns udp port belongs to the tool
     * @param toolId
     * @return
     */
    public static int getUdpPort(int toolId) {
        return BASE_UDP_PORT + toolId * 2;
    }

    public static int getUdpPort(int groupId, int toolId) {
        return getUdpPort(getReqToolId(groupId, toolId));
    }

}
