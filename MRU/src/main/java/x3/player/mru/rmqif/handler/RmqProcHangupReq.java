package x3.player.mru.rmqif.handler;

import x3.player.mru.rmqif.handler.base.RmqIncomingMessageHandler;
import x3.player.mru.rmqif.types.RmqMessage;
import x3.player.mru.room.RoomManager;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionManager;
import x3.player.mru.simulator.UdpRelayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcHangupReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcHangupReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return false;
        }

        releaseResources(msg.getSessionId());

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId());

        return false;
    }

    @Override
    public void sendResponse(String sessionId, long transactionId, int reasonCode, String reasonStr) {

        RmqProcHangupRes res = new RmqProcHangupRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send() == false) {
            // TODO
        }

    }

    private boolean releaseResources(String sessionId) {
        if (sessionId == null) {
            return false;
        }

        SessionManager sessionManager = SessionManager.getInstance();

        SessionInfo sessionInfo = sessionManager.getSession(sessionId);

        if (sessionInfo == null) {
            logger.warn("[{}] No session found", sessionId);
            return false;
        }

        UdpRelayManager udpRelayManager = UdpRelayManager.getInstance();
        udpRelayManager.close(sessionId);

        if (sessionInfo.getConferenceId() != null) {
            RoomManager.getInstance().removeSession(sessionInfo.getConferenceId(), sessionId);
        }

        sessionManager.deleteSession(sessionId);

        return true;
    }
}

