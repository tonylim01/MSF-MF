package com.uangel.acs.rmqif.handler;

import com.uangel.acs.AppInstance;
import com.uangel.acs.config.AmfConfig;
import com.uangel.acs.rmqif.handler.base.RmqOutgoingMessage;
import com.uangel.acs.rmqif.types.RmqMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcInboundSetOfferRes extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcInboundSetOfferRes.class);

    public RmqProcInboundSetOfferRes(String sessionId, long transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_INBOUND_SET_OFFER_RES);
    }

    public boolean send() {
        return sendTo(RMQ_TARGET_ID_MCUD);
    }
}
