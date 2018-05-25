package x3.player.mru.surfif.module;

import x3.player.mru.surfif.handler.SurfProcToolReq;
import x3.player.mru.surfif.messages.SurfMsgEvent;
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
                SurfMsgParticipant.PAR_TYPE_REGULAR,
                mixerId,
                -1,
                SurfMsgParticipant.PAR_ACTION_ADD);
    }

    public void setWhisper(int toolId, int mixerId, int whisperTo) {
        toolReq.setToolType(SurfConstant.TOOL_TYPE_VOICE_MIXER);
        toolReq.addParticipant(toolId,
                SurfMsgParticipant.PAR_TYPE_WHISPER,
                mixerId,
                whisperTo,
                SurfMsgParticipant.PAR_ACTION_ADD);
    }

    public void setChannel(int mixerId,
                           int inPayloadId, int outPayloadId,
                           int localPort,
                           String remoteIp, int remotePort)
    {
        toolReq.setMixerId(mixerId);
        if (mixerId < 0) {
            toolReq.setToolType(SurfConstant.TOOL_TYPE_VOICE_P2P);
        }
        else {
            toolReq.setToolType(SurfConstant.TOOL_TYPE_VOICE_FE_IP);
            toolReq.setBackendToolId(mixerId);
        }
        toolReq.setLocalRtpInfo(localPort, outPayloadId);
        toolReq.setRemoteRtpInfo(remoteIp, remotePort, inPayloadId);
    }

    public void setDtmf(String sessionId, int payload2833) {
        toolReq.setPayload2833(payload2833);
        toolReq.addEvent(SurfMsgEvent.EVENT_TYPE_ALL, true);
        toolReq.setAppInfo(sessionId);
    }

    public void setCoder(String encoder, String decoder, int encSampleRate, int decSampleRate, boolean inputFromRtp) {
        // TODO:
        toolReq.setDecoder(decoder, null, null, decSampleRate);
        toolReq.setEncoder(encoder, null, null,
                !inputFromRtp ? 20 : 0, encSampleRate);    // TODO

        if (!inputFromRtp) {
            toolReq.setInputFromRtp(inputFromRtp);
        }
    }

    public void setVad(boolean enabled) {
        toolReq.setVad(enabled);
        if (enabled) {
            toolReq.addEvent("all", true);
            toolReq.addStatus("all", 10000);
        }
    }


    public void setOverrideSrcPort(int srcPort) {
        toolReq.setOverrideSrcPort(srcPort);
    }

    public void setAgc(int minLevel, int maxLevel) {
        toolReq.setAgc(minLevel, maxLevel);
    }

    public void disableAgc() {
        toolReq.disableAgc();
    }

    public String build() {
        return toolReq.build();
    }

}
