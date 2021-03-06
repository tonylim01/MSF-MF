package x3.player.mru.rmqif.handler;

import x3.player.core.sdp.*;
import x3.player.mru.AppInstance;
import x3.player.mru.config.AmfConfig;
import x3.player.mru.config.SdpConfig;
import x3.player.mru.rmqif.handler.base.RmqOutgoingMessage;
import x3.player.mru.rmqif.messages.InboundGetAnswerRes;
import x3.player.mru.rmqif.types.RmqMessageType;
import x3.player.mru.room.RoomInfo;
import x3.player.mru.room.RoomManager;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.surfif.module.SurfChannelManager;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RmqProcInboundGetAnswerRes extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcInboundGetAnswerRes.class);

    public RmqProcInboundGetAnswerRes(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_INBOUND_GET_ANSWER_RES);
    }

    /**
     * Makes a response body and sends the message to MCUD
     * @return
     */
    public boolean send(String queueName) {

        SessionInfo sessionInfo = checkAndGetSession(getSessionId());
        if (sessionInfo == null) {
            return sendTo(queueName);
        }

        String sdpStr = makeSdp(sessionInfo);
        if (sdpStr == null) {
            logger.error("[{}] Cannot make SDP", getSessionId());

            setReason(RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE, "SDP FAILURE");
            return sendTo(queueName);
        }

        //
        // TODO
        //

        InboundGetAnswerRes res = new InboundGetAnswerRes();
        res.setSdp(sdpStr);

        setBody(res, InboundGetAnswerRes.class);

        return sendTo(queueName);
    }

    /**
     * Makes a SDP body from the remote SDP media attributes
     * @param sessionInfo
     * @return
     */
    private String makeSdp(SessionInfo sessionInfo) {

        if (sessionInfo == null) {
            return null;
        }

        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
        if (roomInfo == null) {
            return null;
        }

        AmfConfig config = AppInstance.getInstance().getConfig();
        SdpConfig sdpConfig = AppInstance.getInstance().getConfig().getSdpConfig();

        SdpBuilder builder = new SdpBuilder();
        builder.setHost(sdpConfig.getLocalHost());

//        builder.setLocalIpAddress(config.getSurfIp());
        builder.setLocalIpAddress(sdpConfig.getLocalIpAddress());
        builder.setSessionName("-");      // TODO

        SdpAttribute attr = selectSdp(sessionInfo);
        if (attr != null) {

            int localPort = sessionInfo.getSrcLocalPort();
            builder.setLocalPort(localPort);

            builder.addRtpAttribute(attr.getPayloadId(), attr.getDescription());

            SdpAttribute dtmfAttr = getTelephonyEvent(sessionInfo);
            if (dtmfAttr != null) {
                builder.addRtpAttribute(dtmfAttr.getPayloadId(), dtmfAttr.getDescription());
                if (sessionInfo.getSdpInfo() != null &&
                        sessionInfo.getSdpInfo().getPayload2833() != dtmfAttr.getPayloadId()) {
                    logger.info("[{}] Update 2833 payload {} -> {}", sessionInfo.getSessionId(),
                            sessionInfo.getSdpInfo().getPayload2833(),  dtmfAttr.getPayloadId());

                    sessionInfo.getSdpInfo().setPayload2833(dtmfAttr.getPayloadId());
                }
            }

            logger.debug("[{}] Select SDP: payload {} local port {}", sessionInfo.getSessionId(),
                    attr.getPayloadId(), localPort);

            for (SdpAttribute sdpAttribute: sessionInfo.getSdpInfo().getAttributes()) {
                if (sdpAttribute.getName() == null) {
                    continue;
                }

                boolean isAppend = false;
                if (sdpAttribute.getName().equals(SdpAttribute.NAME_RTPMAP)) {
                    logger.debug("[{}] makeSDP: payload {} dtmf {} attr {}", sessionInfo.getSessionId(),
                            attr.getPayloadId(),
                            (dtmfAttr != null) ? dtmfAttr.getPayloadId() : "-",
                            sdpAttribute.getPayloadId());

                    if (dtmfAttr != null && sdpAttribute.getDescription() != null &&
                            sdpAttribute.getDescription().contains(String.valueOf(dtmfAttr.getPayloadId()))) {
                        isAppend = true;
                    }
//                    else if (sdpAttribute.getPayloadId() == attr.getPayloadId()) {
//                        isAppend = true;
//                    }
                }
                else if (sdpAttribute.getName().equals(SdpAttribute.NAME_SENDRECV)) {
                    isAppend = true;
                }
                else if (sdpAttribute.getName().equals(SdpAttribute.NAME_FMTP)) {
                    if (sdpAttribute.getDescription() != null) {
                        if (dtmfAttr != null &&
                                sdpAttribute.getDescription().contains(String.valueOf(dtmfAttr.getPayloadId()))) {
                            isAppend = true;
                        }
                        else if (sdpAttribute.getDescription().contains(String.valueOf(attr.getPayloadId()))) {
                            sdpAttribute.setDescription(String.format("%d mode-set=8; octet-align=1", attr.getPayloadId()));
                            isAppend = true;
                        }

                    }
                }

                if (isAppend) {
                    builder.addGeneralAttribute(SdpUtil.getAttributeString(sdpAttribute));
                }
            }
        }
        else {  // Outbound case

//            int localPort = SurfChannelManager.getUdpPort(roomInfo.getGroupId(), SurfChannelManager.TOOL_ID_CD);
            int localPort = sessionInfo.getSrcLocalPort();
            builder.setLocalPort(localPort);

            for (String desc: sdpConfig.getAttributes()) {
                if (desc == null) {
                    continue;
                }

                attr = SdpUtil.parseAttribute(desc);
                if (attr == null) {
                    continue;
                }

                if (attr.getPayloadId() != SdpAttribute.PAYLOADID_NONE) {
                    builder.addRtpAttribute(attr.getPayloadId(), attr.getDescription());
                }
                else {
                    builder.addGeneralAttribute(attr.getDescription());
                }
            }
        }


        return builder.build();
    }

    /**
     * Selects a proper payload comparing to the config's priorities
     * @param sessionInfo
     * @return
     */
    private SdpAttribute selectSdp(SessionInfo sessionInfo) {
        // Compares the SDP media list with the local priorities which read from the config
        SdpAttribute attr = null;
        SdpInfo sdpInfo = sessionInfo.getSdpInfo();

        if (sdpInfo == null) {
            // Outbound case
            return null;
        }

//        SdpParser.selectAttribute(sdpInfo);

        if (sdpInfo.getAttributes() != null) {
            List<Integer> mediaPriorities = AppInstance.getInstance().getConfig().getMediaPriorities();

            if (mediaPriorities != null && mediaPriorities.size() > 0) {
                for (Integer priority : mediaPriorities) {
                    attr = sdpInfo.getAttribute(priority);

                    if (attr != null) {
                        String desc = attr.getDescription();
                        if (desc != null && desc.contains("/")) {
                            String codec = desc.substring(0, desc.indexOf('/')).trim();
                            String sampleRate = desc.substring(desc.indexOf('/' + 1)).trim();

                            if (codec != null) {
                                sdpInfo.setCodecStr(SdpUtil.getCodecStr(codec));
                            }
                        }
                        sdpInfo.setPayloadId(priority);
                        if (sdpInfo.getCodecStr() == null) {
                            sdpInfo.setCodecStr(SdpUtil.getCodecStr(priority));
                        }
                        break;
                    }
                }
            }
        }

        return attr;
    }

    private SdpAttribute getTelephonyEvent(SessionInfo sessionInfo) {
        SdpAttribute attr = null;
        SdpInfo sdpInfo = sessionInfo.getSdpInfo();

        if (sdpInfo == null) {
            // Outbound case
            return null;
        }

        if (sdpInfo.getAttributes() != null) {
            List<SdpAttribute> attributes = sdpInfo.getAttributes();
            for (SdpAttribute attribute: attributes) {
                if (attribute.getDescription() != null &&
                        attribute.getDescription().equals(SdpAttribute.DESC_TELEPHONY_EVENT)) {
                    attr = attribute;
                    break;
                }
            }
        }

        return attr;
    }
}
