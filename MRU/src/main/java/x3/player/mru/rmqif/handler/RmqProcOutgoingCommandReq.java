package x3.player.mru.rmqif.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.rmqif.handler.base.RmqOutgoingMessage;
import x3.player.mru.rmqif.messages.CommandStartReq;
import x3.player.mru.rmqif.messages.FileData;
import x3.player.mru.rmqif.types.RmqMessageType;
import x3.player.mru.session.SessionInfo;

public class RmqProcOutgoingCommandReq extends RmqOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(RmqProcOutgoingCommandReq.class);

    public RmqProcOutgoingCommandReq(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_COMMAND_REQ);
    }

    public void setPlayDone(int channel) {
        SessionInfo sessionInfo = checkAndGetSession(getSessionId());
        if (sessionInfo == null) {
            return;
        }

        CommandStartReq req = new CommandStartReq();
        req.setType(CommandStartReq.CMD_TYPE_MEDIA_DONE);
        req.setChannel(channel);

        setBody(req, CommandStartReq.class);

    }
    /**
     * Sends a CommandReq to the given queue
     * @param queueName
     * @return
     */
    public boolean send(String queueName) {
        return sendTo(queueName);
    }
}
