package x3.player.mru.rmqif.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.rmqif.handler.base.RmqOutgoingMessage;
import x3.player.mru.rmqif.types.RmqMessageType;

public class RmqProcCommandStopRes extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcCommandStopRes.class);

    public RmqProcCommandStopRes(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_COMMAND_STOP_RES);
    }

    public boolean send(String queueName) {
        return sendTo(queueName);
    }

}
