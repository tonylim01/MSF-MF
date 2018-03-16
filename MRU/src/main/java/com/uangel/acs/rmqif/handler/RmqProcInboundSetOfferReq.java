package com.uangel.acs.rmqif.handler;

import com.uangel.acs.rmqif.handler.base.RmqIncomingMessageHandler;
import com.uangel.acs.rmqif.messages.InboundSetOfferReq;
import com.uangel.acs.rmqif.types.RmqMessage;
import com.uangel.acs.rmqif.types.RmqMessageType;
import com.uangel.acs.session.SessionInfo;
import com.uangel.acs.session.SessionManager;
import com.uangel.core.rabbitmq.message.RmqData;
import com.uangel.core.sdp.SdpInfo;
import com.uangel.core.sdp.SdpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcInboundSetOfferReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcInboundSetOfferReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return false;
        }

        RmqData<InboundSetOfferReq> data = new RmqData<>(InboundSetOfferReq.class);
        InboundSetOfferReq req = data.parse(msg);

        if (req == null) {
            logger.error("InboundSetOfferReq: parsing failed");
            return false;
        }

        logger.info("[{}] InboundSetOfferReq: from [{}] to [{}] cnfid [{}]", msg.getSessionId(),
                req.getFromNo(), req.getToNo(), req.getConferenceId());

        if (req.getConferenceId() == null) {
            logger.warn("[{}] InboundSetOfferReq: No conferenceId", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_WRONG_PARAM,
                    "NO CONFERENCE ID");

            return false;
        }

        SdpParser sdpParser = new SdpParser();
        try {
            SdpInfo sdpInfo = sdpParser.parse(req.getSdp());

            //
            // TODO
            //
        } catch (Exception e) {
            e.printStackTrace();
        }

        SessionInfo sessionInfo = SessionManager.getSessionManager().createSession(msg.getSessionId());

        //
        // TODO
        //

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId());

        return false;
    }

    @Override
    public void sendResponse(String sessionId, long transactionId, int reasonCode, String reasonStr) {

        RmqProcInboundSetOfferRes res = new RmqProcInboundSetOfferRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send() == false) {
            // TODO
        }
    }
}

