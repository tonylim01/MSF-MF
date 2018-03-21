package x3.player.mru.rmqif.handler;

import x3.player.mru.AppInstance;
import x3.player.mru.config.SdpConfig;
import x3.player.mru.rmqif.handler.base.RmqOutgoingMessage;
import x3.player.mru.rmqif.messages.InboundGetAnswerRes;
import x3.player.mru.rmqif.types.RmqMessageType;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionManager;
import x3.player.core.sdp.SdpAttribute;
import x3.player.core.sdp.SdpBuilder;
import x3.player.core.sdp.SdpInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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

        SessionInfo sessionInfo = SessionManager.getInstance().getSession(getSessionId());
        if (sessionInfo == null) {
            logger.error("[{}] No session found", getSessionId());
            SessionManager.getInstance().printSessionList();

            if (getHeader().getReasonCode() == RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_SUCCESS) {
                setReason(RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_WRONG_PARAM, "NO SESSION FOUND");
            }
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

        SdpConfig config = AppInstance.getInstance().getConfig().getSdpConfig();

        SdpBuilder builder = new SdpBuilder();
        builder.setHost(config.getLocalHost());
        builder.setLocalIpAddress(sessionInfo.getLocalIpAddress());
        builder.setLocalPort(sessionInfo.getLocalPort());
        builder.setSessionName("acs-mru");      // TODO

        SdpAttribute attr = selectSdp(sessionInfo);
        if (attr != null) {
            builder.addRtpAttribute(attr.getPayloadId(), attr.getDescription());
        }
        else {  // Outbound case
            //
            // TODO
            // Belows are test codes
//            SDP_LOCAL_ATTR_0 = rtpmap:0 PCMU/8000
//            SDP_LOCAL_ATTR_1 = rtpmap:8 PCMA/8000
//            SDP_LOCAL_ATTR_2 = rtpmap:4 G723/8000
//            SDP_LOCAL_ATTR_3 = rtpmap:18 G729/8000
//            SDP_LOCAL_ATTR_4 = ptime:20
//            SDP_LOCAL_ATTR_5 = rtpmap:101 telephone-event/8000
//            SDP_LOCAL_ATTR_6 = fmtp:101 0-16
//            SDP_LOCAL_ATTR_7 = sendrecv
//            SDP_LOCAL_ATTR_8 = direction:active
            builder.addRtpAttribute(8, "PCMA/8000");
            builder.addRtpAttribute(0, "PCMU/8000");
            builder.addRtpAttribute(101, "telephone-event/8000");
            builder.addGeneralAttribute("ptime:20" );
            builder.addGeneralAttribute("fmtp:101 0-16");
            builder.addGeneralAttribute("sendrecv");
            builder.addGeneralAttribute("direction:active");
        }

        if (sessionInfo.getSdpInfo() != null) {

            for (SdpAttribute sdpAttribute: sessionInfo.getSdpInfo().getAttributes()) {
                if (sdpAttribute.getPayloadId() == SdpAttribute.PAYLOADID_NONE) {
                    builder.addGeneralAttribute(sdpAttribute.getDescription() != null ? sdpAttribute.getDescription() : sdpAttribute.getName());
                }
            }
        }
        else {  // Outbound case

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
        else if (sdpInfo.getAttributes() != null) {
            List<Integer> mediaPriorities = AppInstance.getInstance().getConfig().getMediaPriorities();

            if (mediaPriorities != null && mediaPriorities.size() > 0) {
                for (Integer priority : mediaPriorities) {
                    attr = sdpInfo.getAttribute(priority);
                    if (attr != null) { // SDP found
                        break;
                    }
                }
            }
        }

        return attr;
    }
}
