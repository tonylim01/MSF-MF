package x3.player.mru.rmqif.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.rmqif.handler.base.RmqOutgoingMessage;
import x3.player.mru.rmqif.messages.CommandStartReq;
import x3.player.mru.rmqif.messages.DtmfDetectReq;
import x3.player.mru.rmqif.types.RmqMessageType;
import x3.player.mru.session.SessionInfo;

public class RmqProcDtmfDetectReq extends RmqOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(RmqProcDtmfDetectReq.class);

    public RmqProcDtmfDetectReq(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_DTMF_DETECT_REQ);
    }

    public void setDtmfInfo(String mdn, String dtmfName) {
        SessionInfo sessionInfo = checkAndGetSession(getSessionId());
        if (sessionInfo == null) {
            return;
        }

        DtmfDetectReq req = new DtmfDetectReq();
        req.setDtmf(DtmfDetectReq.getDtmfByName(dtmfName));
        req.setMdn(mdn);

        setBody(req, DtmfDetectReq.class);

    }

    /**
     * Sends a DtmfDetReq to the given queue
     * @param queueName
     * @return
     */
    public boolean send(String queueName) {
        return sendTo(queueName);
    }
}
