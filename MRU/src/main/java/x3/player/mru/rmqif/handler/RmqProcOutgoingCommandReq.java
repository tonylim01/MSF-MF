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

    /**
     * Sends a HangupReq to the given queue
     * @param queueName
     * @return
     */
    public boolean send(String queueName) {

        SessionInfo sessionInfo = checkAndGetSession(getSessionId());
        if (sessionInfo == null) {
            return sendTo(queueName);
        }

        CommandStartReq req = new CommandStartReq();
        req.setType(CommandStartReq.CMD_TYPE_MEDIA_DONE);
        req.setChannel(sessionInfo.isBgmPlaying() ? FileData.CHANNEL_BGM : FileData.CHANNEL_MENT);

        setBody(req, CommandStartReq.class);

        return sendTo(queueName);
    }
}
