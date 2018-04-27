package x3.player.mru.surfif.handler;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.AppInstance;
import x3.player.mru.config.SurfConfig;
import x3.player.mru.surfif.messages.*;
import x3.player.mru.surfif.module.SurfConnectionManager;
import x3.player.mru.surfif.module.SurfJsonMessage;
import x3.player.mru.surfif.types.SurfConstant;
import x3.player.mru.surfif.types.SurfEndpointType;

public class SurfProcToolReq extends SurfProcRequest {

    private static final Logger logger = LoggerFactory.getLogger(SurfProcToolReq.class);

    private SurfMsgToolReq msg = null;

    public SurfProcToolReq(int toolId) {
        initMessage(toolId);
    }

    private void initMessage(int toolId) {
        msg = new SurfMsgToolReq();

        msg.setReqId(getReqId());
        msg.setReqType(SurfConstant.REQ_TYPE_SET_CONFIG);
        msg.setToolId(toolId);

        SurfMsgToolReqData data = new SurfMsgToolReqData();
        data.setBackendToolId(-1);  // -1 = Not defined
    }

    public void setReqType(String reqType) {
        msg.setReqType(reqType);
    }

    public void setMixerId(int mixerId) {
        SurfMsgToolReqData data = new SurfMsgToolReqData();
        data.setBackendToolId(mixerId);
    }

    public void setToolType(String toolType) {
        SurfMsgToolReqData data = msg.getData();
        data.setToolType(toolType);
    }

    public void setBackendToolId(int toolId) {
        SurfMsgToolReqData data = msg.getData();
        data.setBackendToolId(toolId);
    }

    public void setInputFromRtp(boolean inputFromRtp) {
        SurfMsgToolReqData data = msg.getData();
        data.setInputFromRtp(inputFromRtp);
    }

    private void setVocoder(SurfMsgVocoder vocoder, String codec, String rate, String packing, int sampleRate) {
        if (vocoder == null) {
            return;
        }

        vocoder.setVocoder(codec);

        if (rate == null) {
            if (codec.equals(SurfMsgVocoder.VOCODER_EVS)) {
                rate = SurfMsgVocoder.RATE_EVS_960;
            }
        }

        if (packing == null) {
            if (codec.equals(SurfMsgVocoder.VOCODER_AMR_WB)) {
                packing = SurfMsgVocoder.PACKING_AMR_OA;
            }
        }

        if (rate != null) {
            vocoder.setRate(rate);
        }
        if (packing != null) {
            vocoder.setPacking(packing);
        }
        if (sampleRate > 0) {
            vocoder.setSampleRate(sampleRate);
        }
    }

    public void setEncoder(String codec, String rate, String packing, int packetDuration, int sampleRate) {
        if (codec == null) {
            logger.error("setEncoder error. Null codec");
            return;
        }

        if (msg.getData().getEncoder() == null) {
            msg.getData().newEncoder();
        }
        setVocoder(msg.getData().getEncoder(), codec, rate, packing, sampleRate);
        msg.getData().getEncoder().setPacketDuration(packetDuration);
    }

    public void setDecoder(String codec, String rate, String packing, int sampleRate) {
        if (codec == null) {
            logger.error("setDecoder error. Null codec");
            return;
        }

        if (msg.getData().getDecoder() == null) {
            msg.getData().newDecoder();
        }

        setVocoder(msg.getData().getDecoder(), codec, rate, packing, sampleRate);
    }

    public void setLocalRtpInfo(int localPort, int outPayloadId) {
        if (msg.getData().getRtp() == null) {
            msg.getData().newRtp();
        }
        SurfMsgRtp rtp = msg.getData().getRtp();
        rtp.setLocalUdpPort(localPort);
        rtp.setOutPayloadType(outPayloadId);
    }

    public void setRemoteRtpInfo(String remoteIp, int remotePort, int inPayloadId) {
        if (msg.getData().getRtp() == null) {
            msg.getData().newRtp();
        }
        SurfMsgRtp rtp = msg.getData().getRtp();
        rtp.setRemoteIp(remoteIp);
        rtp.setRemoteUdpPort(remotePort);
        rtp.setInPayloadType(inPayloadId);
    }

    public void setOverrideSrcPort(int srcPort) {
        if (msg.getData().getRtp() != null) {
            msg.getData().getRtp().setOverrideUdpSrcPort(srcPort);
        }
    }

    public void setAgc(int minLevel, int maxLevel) {
        msg.getData().setAgcDecoder(true,  1000,
                minLevel, maxLevel,
                -1, 0);
    }

    public void setVad(boolean enabled) {
        msg.getData().setVad(enabled);
        if (enabled) {
            setAgc(0, 0);
        }
    }

    public void addEvent(String type, boolean enabled) {
        msg.getData().addEvent(type, enabled);
    }

    public void addStatus(String type, int period) {
        msg.getData().addStatus(type, period);
    }

    public void setSamplingRate(int sampleRate) {
        msg.getData().setSamplingRate(sampleRate);
    }

    public void setHangoverPeriod(int period) {
        msg.getData().setHangoverPeriod(period);
    }

    public void setDominantSpeakers(int numDominant) {
        msg.getData().setDominantSpeakers(numDominant);
    }

    public void setAudioEnabled(boolean audioEnabled) {
        msg.getData().setAudioEnabled(audioEnabled);
    }

    public void setAudioDstToolId(int audioDstToolId) {
        msg.getData().setAudioDstToolId(audioDstToolId);
    }

    public void setCmdType(String cmdType) {
        msg.getData().setCmdType(cmdType);
    }

    public void addFile(String name, float duration, String format, int segment) {
        msg.getData().addFile(name, duration, format, segment);
    }

    public void addParticipant(int id, String type, int toolId, int whisperTo, String action) {
        msg.getData().addParticipant(id, type, toolId, whisperTo, action);
    }

    public void setRepetitions(int repetitions) {
        msg.getData().setRepetitions(repetitions);
    }

    public void setDuration(float duration) {
        msg.getData().setDuration(duration);
    }

    /*
    public String build() {

        Gson gson = new Gson();
        String json = null;

        try {
            json = gson.toJson(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }
    */

    public String build() {

        SurfJsonMessage<SurfMsgToolReq> jsonMessage = new SurfJsonMessage<>(SurfMsgToolReq.class);
        String jsonStr = jsonMessage.build(SurfMsgToolReq.MSG_NAME, msg);

        return jsonStr;

    }


}
