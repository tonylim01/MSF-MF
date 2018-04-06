package x3.player.mru.surfif.module;

import com.google.gson.Gson;
import x3.player.mru.surfif.messages.*;
import x3.player.mru.surfif.types.SurfConstant;
import x3.player.mru.surfif.types.SurfEndpointType;

public class SurfChannelBuilder {

    private SurfMsgVoiceConfig msg;

    public SurfChannelBuilder(int toolId, int reqId) {
        msg = new SurfMsgVoiceConfig();

        SurfMsgToolReq toolReq = msg.getToolReq();

        toolReq.setToolId(toolId);
        toolReq.setReqId(reqId);
        toolReq.setReqType(SurfConstant.REQ_TYPE_SET_CONFIG);

    }

    public void setToolType(SurfEndpointType endpointType) {
        SurfMsgToolReqData data = msg.getToolReq().getData();

        data.setToolType((endpointType == SurfEndpointType.ENDPOINT_TYPE_P2P) ?
                SurfMsgToolReqData.TOOL_TYPE_VOICE_P2P : SurfMsgToolReqData.TOOL_TYPE_VOICE_FE_IP);
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
        setVocoder(msg.getToolReq().getData().getEncoder(), codec, rate, packing);
    }

    public void setDecoder(String codec, String rate, String packing) {
        setVocoder(msg.getToolReq().getData().getDecoder(), codec, rate, packing);
    }

    public void setLocalRtpInfo(int localPort, int outPayloadId) {
        SurfMsgRtp rtp = msg.getToolReq().getData().getRtp();
        rtp.setLocalUdpPort(localPort);
        rtp.setOutPayloadType(outPayloadId);
    }

    public void setRemoteRtpInfo(String remoteIp, int remotePort, int inPayloadId) {
        SurfMsgRtp rtp = msg.getToolReq().getData().getRtp();
        rtp.setRemoteIp(remoteIp);
        rtp.setRemoteUdpPort(remotePort);
        rtp.setInPayloadType(inPayloadId);
    }

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


    public boolean createVoiceChannel() {

        SurfMsgToolReq toolReq = msg.getToolReq();

        toolReq.setToolId(1);   // TODO
        toolReq.setReqId(0);    // TODO
        toolReq.setReqType(SurfConstant.REQ_TYPE_SET_CONFIG);

        SurfMsgToolReqData data = toolReq.getData();

        data.setToolType(SurfMsgToolReqData.TOOL_TYPE_VOICE_P2P);
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

        return false;
    }
}
