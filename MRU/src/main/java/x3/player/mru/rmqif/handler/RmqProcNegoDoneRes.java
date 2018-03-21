package x3.player.mru.rmqif.handler;

import x3.player.mru.rmqif.handler.base.RmqOutgoingMessage;
import x3.player.mru.rmqif.types.RmqMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcNegoDoneRes extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcNegoDoneRes.class);

    public RmqProcNegoDoneRes(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_NEGO_DONE_RES);
    }

    public boolean send(String queueName) {
        return sendTo(queueName);
    }
}
