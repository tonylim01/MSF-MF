package x3.player.mru.rmqif.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.AppInstance;
import x3.player.mru.config.AmfConfig;
import x3.player.mru.rmqif.handler.base.RmqOutgoingMessage;
import x3.player.mru.rmqif.types.RmqMessageType;
import x3.player.mru.session.SessionInfo;

public class RmqProcOutgoingHangupReq extends RmqOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(RmqProcOutgoingHangupReq.class);

    public RmqProcOutgoingHangupReq(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_HANGUP_REQ);
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

        return sendTo(queueName);
    }

    /**
     * Sends a message to MCUD
     * @return
     */
    public boolean sendToMcud() {
        AmfConfig config = AppInstance.getInstance().getConfig();
        if (config == null) {
            logger.error("[{}] Null config", getSessionId());
            return false;
        }
        return sendTo(config.getMcudName());
    }
}
