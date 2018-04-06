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

    public void setMixerId(int mixerId) {
        SurfMsgToolReqData data = new SurfMsgToolReqData();
        data.setBackendToolId(mixerId);
    }

    public void setToolType(String toolType) {
        SurfMsgToolReqData data = msg.getData();
        data.setToolType(toolType);
    }

    private void setVocoder(SurfMsgVocoder vocoder, String codec, String rate, String packing) {
        if (vocoder == null) {
            return;
        }

        vocoder.setVocoder(codec);
        if (rate != null) {
            vocoder.setRate(rate);
        }
        if (packing != null) {
            vocoder.setPacking(packing);
        }
    }

    public void setEncoder(String codec, String rate, String packing) {
        if (msg.getData().getEncoder() == null) {
            msg.getData().newEncoder();
        }
        setVocoder(msg.getData().getEncoder(), codec, rate, packing);
    }

    public void setDecoder(String codec, String rate, String packing) {
        if (msg.getData().getDecoder() == null) {
            msg.getData().newDecoder();
        }
        setVocoder(msg.getData().getDecoder(), codec, rate, packing);
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

    public void setSamplingRate(int sampleRate) {
        msg.getData().setSamplingRate(sampleRate);
    }

    public void setHangoverPeriod(int period) {
        msg.getData().setHangoverPeriod(period);
    }

    public void setDominantSpeakers(int numDominant) {
        msg.getData().setDominantSpeakers(numDominant);
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