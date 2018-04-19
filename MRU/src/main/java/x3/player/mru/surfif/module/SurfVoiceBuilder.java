package x3.player.mru.surfif.module;

import x3.player.mru.surfif.handler.SurfProcToolReq;
import x3.player.mru.surfif.messages.SurfMsgParticipant;
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

    public void setParticipant(int toolId, int mixerId) {
        toolReq.setToolType(SurfConstant.TOOL_TYPE_VOICE_MIXER);
        toolReq.addParticipant(toolId,
                SurfMsgParticipant.PAR_TYPE_DOMINANT,
                mixerId,
                -1,
                SurfMsgParticipant.PAR_ACTION_ADD);
    }

    public void setChannel(int mixerId, boolean inputFromRtp,
                           int inPayloadId, int outPayloadId,
                           int localPort,
                           String remoteIp, int remotePort,
                           boolean enableVad)
    {
        toolReq.setMixerId(mixerId);
        if (mixerId < 0) {
            toolReq.setToolType(SurfConstant.TOOL_TYPE_VOICE_P2P);
        }
        else {
            toolReq.setToolType(SurfConstant.TOOL_TYPE_VOICE_FE_IP);
            toolReq.setBackendToolId(mixerId);
        }
        if (!inputFromRtp) {
            toolReq.setInputFromRtp(inputFromRtp);
        }
        toolReq.setDecoder(inputFromRtp ? SurfMsgVocoder.VOCODER_ALAW : SurfMsgVocoder.VOCODER_LINEAR,
                null, null);
        toolReq.setEncoder(SurfMsgVocoder.VOCODER_ALAW, null, null,
                !inputFromRtp ? 20 : 0);    // TODO
        toolReq.setLocalRtpInfo(localPort, outPayloadId);
        toolReq.setRemoteRtpInfo(remoteIp, remotePort, inPayloadId);

        if (enableVad) {
            toolReq.setVad(true);
            toolReq.addEvent("all", true);  // TODO
        }
    }

    public void setOverrideSrcPort(int srcPort) {
        toolReq.setOverrideSrcPort(srcPort);
    }

    public void setAgc(int minLevel, int maxLevel) {
        toolReq.setAgc(minLevel, maxLevel);
    }

    public String build() {
        return toolReq.build();
    }

}
