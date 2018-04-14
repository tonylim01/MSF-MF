package x3.player.mru.surfif.module;

import x3.player.mru.surfif.handler.SurfProcToolReq;
import x3.player.mru.surfif.messages.SurfMsgVocoder;
import x3.player.mru.surfif.types.SurfConstant;

public class SurfVoiceBuilder {
    SurfProcToolReq toolReq = null;

    public SurfVoiceBuilder(int toolId) {
        toolReq = new SurfProcToolReq(toolId);
    }

    public void setToolType(String toolType) {
        toolReq.setToolType(toolType);
    }

    public void setMixer(int sampleRate, int hangoverPeriod, int dominantSpeakers) {
        toolReq.setToolType(SurfConstant.TOOL_TYPE_VOICE_MIXER);
        toolReq.setSamplingRate(sampleRate);
        toolReq.setHangoverPeriod(hangoverPeriod);
        toolReq.setDominantSpeakers(dominantSpeakers);
    }

    public void setChannel(int mixerId, boolean inputFromRtp,
                           int inPayloadId, int outPayloadId,
                           int localPort,
                           String remoteIp, int remotePort)
    {
        toolReq.setMixerId(mixerId);
        toolReq.setToolType((mixerId < 0) ?
                SurfConstant.TOOL_TYPE_VOICE_P2P : SurfConstant.TOOL_TYPE_VOICE_FE_IP);
        if (!inputFromRtp) {
            toolReq.setInputFromRtp(inputFromRtp);
        }
        toolReq.setDecoder(SurfMsgVocoder.VOCODER_ALAW, null, null);
        toolReq.setEncoder(SurfMsgVocoder.VOCODER_ALAW, null, null);
        toolReq.setLocalRtpInfo(localPort, outPayloadId);
        toolReq.setRemoteRtpInfo(remoteIp, remotePort, inPayloadId);
    }

    public String build() {
        return toolReq.build();
    }

}
