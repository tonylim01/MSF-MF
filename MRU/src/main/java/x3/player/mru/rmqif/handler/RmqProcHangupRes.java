package x3.player.mru.rmqif.handler;

import x3.player.mru.rmqif.handler.base.RmqOutgoingMessage;
import x3.player.mru.rmqif.messages.HangupRes;
import x3.player.mru.rmqif.types.RmqMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcHangupRes extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcHangupRes.class);

    public RmqProcHangupRes(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_HANGUP_RES);
    }

    public boolean send(String queueName) {

        HangupRes res = new HangupRes();

        res.setConferenceId(null);  // TODO
        res.setParticipantCount(0); // TODO

        setBody(res, HangupRes.class);

        return sendTo(queueName);
    }
}
