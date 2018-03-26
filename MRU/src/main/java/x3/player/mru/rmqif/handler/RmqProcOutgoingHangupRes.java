package x3.player.mru.rmqif.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.rmqif.handler.base.RmqIncomingMessageHandler;
import x3.player.mru.rmqif.types.RmqMessage;
import x3.player.mru.service.ServiceManager;
import x3.player.mru.session.SessionInfo;

public class RmqProcOutgoingHangupRes extends RmqIncomingMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(RmqProcOutgoingHangupRes.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return false;
        }

        SessionInfo sessionInfo = validateSessionId(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());
        if (sessionInfo == null) {
            logger.error("[{}] Session not found", msg.getSessionId());
            return false;
        }

        ServiceManager.getInstance().releaseResource(msg.getSessionId());

        return true;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

    }
}
