package com.uangel.acs.rmqif.handler;

import com.uangel.acs.rmqif.handler.base.RmqOutgoingMessage;
import com.uangel.acs.rmqif.types.RmqMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcNegoDoneRes extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcNegoDoneRes.class);

    public RmqProcNegoDoneRes(String sessionId, long transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_NEGO_DONE_RES);
    }

    public boolean send() {
        return sendTo(RMQ_TARGET_ID_MCUD);
    }
}
