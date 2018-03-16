package com.uangel.acs.rmqif.handler;

import com.uangel.acs.rmqif.handler.base.RmqIncomingMessageHandler;
import com.uangel.acs.rmqif.types.RmqMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcHangupReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcHangupReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return false;
        }

        //
        // TODO
        //

        RmqProcHangupRes res = new RmqProcHangupRes(msg.getHeader().getSessionId(), msg.getHeader().getTransactionId());
        if (res.send() == false) {
            // TODO
        }

        return false;
    }

    @Override
    public void sendResponse(String sessionId, long transactionId, int reasonCode, String reasonStr) {

    }
}

