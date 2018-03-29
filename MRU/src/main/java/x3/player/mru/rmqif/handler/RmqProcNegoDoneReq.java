package x3.player.mru.rmqif.handler;

import x3.player.core.sdp.SdpParser;
import x3.player.mru.rmqif.handler.base.RmqIncomingMessageHandler;
import x3.player.mru.rmqif.messages.NegoDoneReq;
import x3.player.mru.rmqif.types.RmqMessage;
import x3.player.mru.rmqif.types.RmqMessageType;
import x3.player.mru.room.RoomInfo;
import x3.player.mru.room.RoomManager;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionManager;
import x3.player.mru.session.SessionState;
import x3.player.mru.session.SessionStateManager;
import x3.player.mru.simulator.UdpRelayManager;
import x3.player.mru.rmqif.module.RmqData;
import x3.player.core.sdp.SdpInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcNegoDoneReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcNegoDoneReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.error("[{}] Invalid message");
            return false;
        }

        SessionInfo sessionInfo = validateSessionId(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());
        if (sessionInfo == null) {
            logger.error("[{}] Session not found", msg.getSessionId());
            return false;
        }

        RmqData<NegoDoneReq> data = new RmqData<>(NegoDoneReq.class);
        NegoDoneReq req = data.parse(msg);

        if (req == null) {
            logger.error("[{}] NegoDoneReq: parsing failed", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "PARSING FAILURE");
            return false;
        }

        logger.info("[{}] NegoDoneReq: sdp [{}]", msg.getSessionId(), req.getSdp());

        if (req.getSdp() != null) {
            SdpInfo sdpInfo = SdpParser.parseSdp(req.getSdp());
            sessionInfo.setSdpInfo(sdpInfo);
        }

        SessionStateManager.getInstance().setState(msg.getSessionId(), SessionState.PREPARE);

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());

        return false;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

        RmqProcNegoDoneRes res = new RmqProcNegoDoneRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send(queueName) == false) {
            // TODO
        }
    }

}

