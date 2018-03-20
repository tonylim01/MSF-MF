package com.uangel.acs.rmqif.handler.base;

import com.uangel.acs.rmqif.types.RmqMessageType;

public abstract class RmqIncomingMessageHandler implements RmqIncomingMessageInterface {

    /**
     * Pre-implements to send a response as a successs
     * @param sessionId
     * @param transactionId
     */
    @Override
    public void sendResponse(String sessionId, long transactionId) {
        sendResponse(sessionId, transactionId, RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_SUCCESS, null);
    }

}
