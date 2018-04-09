package x3.player.mru.rmqif.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.AppInstance;
import x3.player.mru.rmqif.handler.base.RmqIncomingMessageHandler;
import x3.player.mru.rmqif.messages.ServiceStartReq;
import x3.player.mru.rmqif.module.RmqData;
import x3.player.mru.rmqif.types.RmqMessage;
import x3.player.mru.rmqif.types.RmqMessageType;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionState;
import x3.player.mru.session.SessionStateManager;

public class RmqProcServiceStartReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcServiceStartReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return false;
        }

        logger.info("[{}] ServiceStartReq", msg.getSessionId());

        SessionInfo sessionInfo = validateSessionId(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());
        if (sessionInfo == null) {
            logger.error("[{}] Session not found", msg.getSessionId());
            return false;
        }

        RmqData<ServiceStartReq> data = new RmqData<>(ServiceStartReq.class);
        ServiceStartReq req = data.parse(msg);

        if (req == null) {
            logger.error("[{}] ServiceStartReq: parsing failed", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "PARSING FAILURE");
            return false;
        }

        String aiifName = AppInstance.getInstance().getConfig().getRmqAiif(req.getAiifId());
        if (aiifName == null) {
            logger.error("[{}] ServiceStartReq: Invalid aiifId [{}]", msg.getSessionId(), req.getAiifId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "INVALID AIIFID");
            return false;
        }

        sessionInfo.setAiifName(aiifName);
        
        SessionStateManager.getInstance().setState(msg.getSessionId(), SessionState.START);

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());

        return false;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

        RmqProcServiceStartRes res = new RmqProcServiceStartRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send(queueName) == false) {
            // TODO
        }

    }
}