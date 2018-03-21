package x3.player.mru.rmqif.handler;

import x3.player.mru.AppInstance;
import x3.player.mru.rmqif.handler.base.RmqOutgoingMessage;
import x3.player.mru.rmqif.types.RmqMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcInboundSetOfferRes extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcInboundSetOfferRes.class);

    public RmqProcInboundSetOfferRes(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_INBOUND_SET_OFFER_RES);
    }

    public boolean send(String queueName) {
        return sendTo(queueName);
    }
}
