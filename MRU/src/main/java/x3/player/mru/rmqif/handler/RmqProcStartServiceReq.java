package x3.player.mru.rmqif.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.AppInstance;
import x3.player.mru.config.AmfConfig;
import x3.player.mru.rmqif.handler.base.RmqOutgoingMessage;
import x3.player.mru.rmqif.messages.ServiceStartReq;
import x3.player.mru.rmqif.types.RmqMessageType;
import x3.player.mru.session.SessionInfo;

public class RmqProcStartServiceReq extends RmqOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(RmqProcStartServiceReq.class);

    public RmqProcStartServiceReq(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_SERVICE_START_REQ);
    }

    /**
     * Sends a ServiceStartReq to the given queue
     * @param queueName
     * @return
     */
    public boolean send(String queueName) {

        SessionInfo sessionInfo = checkAndGetSession(getSessionId());
        if (sessionInfo == null) {
            return sendTo(queueName);
        }

        ServiceStartReq req = new ServiceStartReq();
        req.setFromNo(sessionInfo.getFromNo());
        req.setToNo(sessionInfo.getToNo());

        setBody(req, ServiceStartReq.class);

        return sendTo(queueName);
    }

    /**
     * Sends a message to ACSWF
     * @return
     */
    public boolean sendToAcswf() {
        AmfConfig config = AppInstance.getInstance().getConfig();
        if (config == null) {
            logger.error("[{}] Null config", getSessionId());
            return false;
        }

       return send(config.getRmqAcswf());
    }

}
