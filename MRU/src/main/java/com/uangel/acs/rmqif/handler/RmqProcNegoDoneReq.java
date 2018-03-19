package com.uangel.acs.rmqif.handler;

import com.uangel.acs.rmqif.handler.base.RmqIncomingMessageHandler;
import com.uangel.acs.rmqif.messages.NegoDoneReq;
import com.uangel.acs.rmqif.types.RmqMessage;
import com.uangel.acs.rmqif.types.RmqMessageType;
import com.uangel.core.rabbitmq.message.RmqData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcNegoDoneReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcNegoDoneReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.error("[{}] Invalid message");
            return false;
        }

        if (msg.getSessionId() == null) {
            logger.error("[{}] No sessionId found");
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_WRONG_PARAM,
                    "NO SESSION ID");
            return  false;
        }

        RmqData<NegoDoneReq> data = new RmqData<>(NegoDoneReq.class);
        NegoDoneReq req = data.parse(msg);

        if (req == null) {
            logger.error("[{}] NegoDoneReq: parsing failed", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "PARSING FAILURE");
            return false;
        }

        logger.info("[{}] NegoDoneReq: sdp [{}]", msg.getSessionId(), req.getSdp());

        //
        // TODO
        //

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId());

        return false;
    }

    @Override
    public void sendResponse(String sessionId, long transactionId, int reasonCode, String reasonStr) {

        RmqProcNegoDoneRes res = new RmqProcNegoDoneRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send() == false) {
            // TODO
        }

    }
}

