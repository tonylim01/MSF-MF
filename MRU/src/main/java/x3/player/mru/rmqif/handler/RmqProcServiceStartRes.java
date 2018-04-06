package x3.player.mru.rmqif.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.rmqif.handler.base.RmqOutgoingMessage;
import x3.player.mru.rmqif.types.RmqMessageType;

public class RmqProcServiceStartRes extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcServiceStartRes.class);

    public RmqProcServiceStartRes(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_SERVICE_START_RES);
    }

    public boolean send(String queueName) {
        return sendTo(queueName);
    }
}
