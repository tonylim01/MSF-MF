package com.uangel.acs.rmqif.handler;

import com.uangel.acs.rmqif.handler.base.RmqIncomingMessageInterface;
import com.uangel.acs.rmqif.types.RmqMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcHangupReq implements RmqIncomingMessageInterface {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcHangupReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return false;
        }

        logger.info("[{}] <- HangupReq", msg.getHeader().getSessionId());

        //
        // TODO
        //

        RmqProcHangupRes res = new RmqProcHangupRes(msg.getHeader().getSessionId(), msg.getHeader().getTransactionId());
        if (res.send() == false) {
            logger.error("[{}] -> HangupRes failed", msg.getHeader().getSessionId());
        }

        return false;
    }

}

