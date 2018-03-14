package com.uangel.acs.rmqif.handler;

import com.uangel.acs.AppInstance;
import com.uangel.acs.config.AmfConfig;
import com.uangel.acs.rmqif.types.RmqMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcInboundSetOfferRes extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcInboundSetOfferRes.class);

    public RmqProcInboundSetOfferRes(String sessionId, long transactionId) {
        super();
        setType(RmqMessageType.RMQ_MSG_STR_INBOUND_SET_OFFER_RES);
        setSessionId(sessionId);
        setTransactionId(transactionId);
    }

    public boolean send() {
        AmfConfig config = AppInstance.getInstance().getConfig();
        if (config == null) {
            return false;
        }

        boolean result = sendTo(config.getMcudName());

        if (result) {
            logger.info("[{}] -> IncomingSetOfferRes", getSessionId());
        }
        else {
            logger.error("[{}] -> IncomingSetOfferRes failed", getSessionId());
        }

        return result;
    }
}
