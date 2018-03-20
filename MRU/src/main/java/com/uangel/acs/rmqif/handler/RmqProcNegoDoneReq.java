package com.uangel.acs.rmqif.handler;

import com.uangel.acs.AppInstance;
import com.uangel.acs.config.SdpConfig;
import com.uangel.acs.rmqif.handler.base.RmqIncomingMessageHandler;
import com.uangel.acs.rmqif.messages.NegoDoneReq;
import com.uangel.acs.rmqif.types.RmqMessage;
import com.uangel.acs.rmqif.types.RmqMessageType;
import com.uangel.acs.room.RoomInfo;
import com.uangel.acs.room.RoomManager;
import com.uangel.acs.session.SessionInfo;
import com.uangel.acs.session.SessionManager;
import com.uangel.acs.simulator.UdpRelayManager;
import com.uangel.core.rabbitmq.message.RmqData;
import com.uangel.core.sdp.SdpInfo;
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

        if (msg.getSessionId() == null) {
            logger.error("[{}] No sessionId found");
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_WRONG_PARAM,
                    "NO SESSION ID");
            return  false;
        }

        RmqData<NegoDoneReq> data = new RmqData<>(NegoDoneReq.class);
        NegoDoneReq req = data.parse(msg);

        if (req == null) {
            logger.error("[{}] NegoDoneReq: parsing failed", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "PARSING FAILURE");
            return false;
        }

        logger.info("[{}] NegoDoneReq: sdp [{}]", msg.getSessionId(), req.getSdp());

        openLocalResource(msg.getSessionId());

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId());

        return false;
    }

    @Override
    public void sendResponse(String sessionId, long transactionId, int reasonCode, String reasonStr) {

        RmqProcNegoDoneRes res = new RmqProcNegoDoneRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send() == false) {
            // TODO
        }
    }

    private boolean openLocalResource(String sessionId) {
        if (sessionId == null) {
            return false;
        }

        SessionManager sessionManager = SessionManager.getInstance();

        SessionInfo sessionInfo = sessionManager.getSession(sessionId);
        if (sessionInfo == null) {
            logger.error("[{}] No sessionInfo found", sessionId);
            return false;
        }

        //
        // TODO
        //
        // Start of Demo Service
        UdpRelayManager udpRelayManager = UdpRelayManager.getInstance();
        udpRelayManager.openServer(sessionId, sessionInfo.getLocalPort());

        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(sessionInfo.getConferenceId());
        if (roomInfo == null) {
            logger.error("[{}] No roomInfo found", sessionId);
            return false;
        }

        String otherSessionId = roomInfo.getOtherSession(sessionId);
        if (otherSessionId != null) {
            logger.info("[{}] Connected to session [{}]", sessionId, otherSessionId);

            SessionInfo otherSessionInfo = sessionManager.getSession(otherSessionId);
            if (otherSessionInfo == null) {
                logger.warn("[{}] No sessionInfo found", otherSessionId);
                return false;
            }

            SdpInfo otherRemoteSdpInfo = otherSessionInfo.getSdpInfo();
            udpRelayManager.openClient(sessionId, otherRemoteSdpInfo.getRemoteIp(), otherRemoteSdpInfo.getRemotePort());

            logger.debug("[{}] Make connection: local [{}] to [{}:{}]", sessionId,
                    sessionInfo.getLocalPort(), otherRemoteSdpInfo.getRemoteIp(), otherRemoteSdpInfo.getRemotePort());

            SdpInfo remoteSdpInfo = sessionInfo.getSdpInfo();
            udpRelayManager.openClient(otherSessionId, remoteSdpInfo.getRemoteIp(), remoteSdpInfo.getRemotePort());

            logger.debug("[{}] Make connection: local [{}] to [{}:{}]", otherSessionId,
                    otherSessionInfo.getLocalPort(), remoteSdpInfo.getRemoteIp(), remoteSdpInfo.getRemotePort());
        }
        // End of Demo service

        return true;
    }
}

