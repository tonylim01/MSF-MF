package x3.player.mru.surfif.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.surfif.messages.*;
import x3.player.mru.surfif.types.SurfEndpointType;

public class SurfChannelManager {
    private static final Logger logger = LoggerFactory.getLogger(SurfChannelManager.class);

    private static final int DEFAULT_GROUP_SIZE = 2;

    public static final int TOOL_ID_MIXER   = 0;
    public static final int TOOL_ID_CALLER  = 1;
    public static final int TOOL_ID_PAR     = 2;
    public static final int TOOL_ID_CALLEE  = 3;
    public static final int TOOL_ID_PLAY    = 4;
    public static final int TOOL_ID_BG      = 5;

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
        this.groupCount = totalChannels / 6;

        if (groupCount <= 0) {
            groupCount = DEFAULT_GROUP_SIZE;
        }

        channelGroups = new SurfChannelGroup[groupCount];

        for (int i = 0; i < groupCount; i++) {
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

    public boolean createVoiceChannel(int toolId, int reqId) {

        SurfChannelBuilder builder = new SurfChannelBuilder(toolId, reqId);

        builder.setToolType(SurfEndpointType.ENDPOINT_TYPE_P2P);
        builder.setDecoder(SurfMsgVocoder.VOCODER_ALAW, null, null);
        builder.setEncoder(SurfMsgVocoder.VOCODER_ALAW, null, null);
        builder.setLocalRtpInfo(10000, 8); // TODO
        builder.setRemoteRtpInfo("192.168.1.30", 30000, 8); // TODO

        String json = builder.build();

        // TODO : Sends json to surf HMP

        return false;
    }

    /**
     * Sample code for reference
     * @param toolId
     * @param reqId
     * @return
     */
    private SurfMsgVoiceConfig getVoiceConfig(int toolId, int reqId) {
        SurfMsgVoiceConfig msg = new SurfMsgVoiceConfig();

        SurfMsgToolReq toolReq = msg.getToolReq();

        toolReq.setToolId(toolId);
        toolReq.setReqId(reqId);
        toolReq.setReqType(SurfMsgToolReq.REQ_TYPE_SET_CONFIG);

        SurfMsgToolData data = toolReq.getData();

        data.setToolType(SurfMsgToolData.TOOL_TYPE_VOICE_P2P);
        data.setBackendToolId(2);   // TODO: mixer's toolId

        SurfMsgVocoder decoder = data.getDecoder();
        decoder.setVocoder(SurfMsgVocoder.VOCODER_ALAW);   // TODO

        SurfMsgVocoder encoder = data.getEncoder();
        encoder.setVocoder(SurfMsgVocoder.VOCODER_ALAW);   // TODO

        SurfMsgRtp rtp = data.getRtp();

        rtp.setLocalUdpPort(10000); // TODO
        rtp.setRemoteUdpPort(10002);    // TODO
        rtp.setRemoteIp("192.168.1.1"); // TODO
        rtp.setInPayloadType(8);    // TODO
        rtp.setOutPayloadType(8);   // TODO

        return msg;
    }
}
