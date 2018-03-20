package com.uangel.acs.rmqif.handler.base;

import com.uangel.acs.rmqif.types.RmqMessage;

public interface RmqIncomingMessageInterface {

    boolean handle(RmqMessage msg);

    /**
     * Sends a response with reason = success
     * @param sessionId
     * @param transactionId
     */
    void sendResponse(String sessionId, long transactionId);

    /**
     * Sends a resopnse with a specified reasonCode and reasonStr
     * @param sessionId
     * @param transactionId
     * @param reasonCode
     * @param reasonStr
     */
    void sendResponse(String sessionId, long transactionId, int reasonCode, String reasonStr);
}
