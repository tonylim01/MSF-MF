package x3.player.mru.rmqif.handler.base;

import x3.player.mru.rmqif.types.RmqMessageType;

public abstract class RmqIncomingMessageHandler implements RmqIncomingMessageInterface {

    /**
     * Calls a sendResponse() with the default queueName
     * @param sessionId
     * @param transactionId
     */
    @Override
    public void sendResponse(String sessionId, String transactionId) {
        sendResponse(sessionId, transactionId, null, RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_SUCCESS, null);
    }

    /**
     * Pre-implements to send a response as a successs
     * @param sessionId
     * @param transactionId
     */
    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName) {
        sendResponse(sessionId, transactionId, queueName, RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_SUCCESS, null);
    }

    /**
     * Calls a sendResponse() with the default queueName
     * @param sessionId
     * @param transactionId
     * @param reasonCode
     * @param reasonStr
     */
    @Override
    public void sendResponse(String sessionId, String transactionId, int reasonCode, String reasonStr) {
        sendResponse(sessionId, transactionId, null, reasonCode, reasonStr);
    }
}
