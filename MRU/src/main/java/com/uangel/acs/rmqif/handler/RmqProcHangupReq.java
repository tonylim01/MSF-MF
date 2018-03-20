package com.uangel.acs.rmqif.handler;

import com.uangel.acs.rmqif.handler.base.RmqIncomingMessageHandler;
import com.uangel.acs.rmqif.types.RmqMessage;
import com.uangel.acs.room.RoomManager;
import com.uangel.acs.session.SessionInfo;
import com.uangel.acs.session.SessionManager;
import com.uangel.acs.simulator.UdpRelayManager;
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

