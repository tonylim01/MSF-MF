package com.uangel.acs.rmqif.handler;

import com.uangel.acs.rmqif.handler.base.RmqIncomingMessageInterface;
    import com.uangel.acs.rmqif.types.RmqMessage;
import com.uangel.core.rabbitmq.message.RmqData;
import com.uangel.core.sdp.SdpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcInboundGetAnswerReq implements RmqIncomingMessageInterface {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcInboundGetAnswerReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return false;
        }

        logger.info("[{}] <- InboundGetAnswerReq", msg.getHeader().getSessionId());

        //
        // TODO
        //

        RmqProcInboundGetAnswerRes res = new RmqProcInboundGetAnswerRes(msg.getHeader().getSessionId(), msg.getHeader().getTransactionId());
        if (res.send() == false) {
            logger.error("[{}] -> InboundSetOfferRes failed", msg.getHeader().getSessionId());
        }

        return false;
    }

}

