package com.uangel.acs.rmqif.handler;

import com.uangel.acs.rmqif.handler.base.RmqIncomingMessageHandler;
import com.uangel.acs.rmqif.messages.InboundSetOfferReq;
import com.uangel.acs.rmqif.types.RmqMessage;
import com.uangel.acs.rmqif.types.RmqMessageType;
import com.uangel.acs.room.RoomManager;
import com.uangel.acs.session.SessionInfo;
import com.uangel.acs.session.SessionManager;
import com.uangel.core.rabbitmq.message.RmqData;
import com.uangel.core.sdp.SdpInfo;
import com.uangel.core.sdp.SdpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcInboundSetOfferReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcInboundSetOfferReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return false;
        }

        RmqData<InboundSetOfferReq> data = new RmqData<>(InboundSetOfferReq.class);
        InboundSetOfferReq req = data.parse(msg);

        if (req == null) {
            logger.error("InboundSetOfferReq: parsing failed");
            return false;
        }

        logger.info("[{}] InboundSetOfferReq: from [{}] to [{}] cnfid [{}]", msg.getSessionId(),
                req.getFromNo(), req.getToNo(), req.getConferenceId());

        // SessionId and ConferenceId are mandatory fields in the service
        if (msg.getSessionId() == null) {
            logger.error("[{}] No sessionId found");
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_WRONG_PARAM,
                    "NO SESSION ID");
            return  false;
        }

        if (req.getConferenceId() == null) {
            logger.warn("[{}] InboundSetOfferReq: No conferenceId", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_WRONG_PARAM,
                    "NO CONFERENCE ID");
            return false;
        }

        // sdpInfo can be null for a no-sdp case
        SdpInfo sdpInfo = parseSdp(req.getSdp());

        boolean result;
        result = setRoomInfo(req.getConferenceId(), msg.getSessionId());
        if (result == false) {
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "ROOM-SESSION ERROR");
            return false;
        }

        // Creates a sessionInfo and set things following with the offerReq
        SessionManager sessionManager = SessionManager.getInstance();
        SessionInfo sessionInfo = sessionManager.createSession(msg.getSessionId());

        if (sessionInfo == null) {
            logger.warn("[{}] Cannot create session", msg.getSessionId());
            RoomManager.getInstance().removeSession(req.getConferenceId(), msg.getSessionId());

            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "SESSION ERROR");
            return false;
        }

        sessionInfo.setSdpInfo(sdpInfo);

        //
        // TODO
        //

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId());

        return true;
    }

    @Override
    public void sendResponse(String sessionId, long transactionId, int reasonCode, String reasonStr) {

        RmqProcInboundSetOfferRes res = new RmqProcInboundSetOfferRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send() == false) {
            // TODO
        }
    }

    /**
     * Parses a SDP body and returns SdpInfo
     * @param sdp
     * @return SdpInfo
     */
    private SdpInfo parseSdp(String sdp) {
        if (sdp == null) {
            return null;
        }

        SdpInfo sdpInfo = null;
        SdpParser sdpParser = new SdpParser();
        try {
            sdpInfo = sdpParser.parse(sdp);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sdpInfo;
    }

    /**
     * Checks a room with the key is a conferenceId.
     * If a room is not found, creates new room and put the sessionId into the room
     * @param conferenceId
     * @param sessionId
     * @return
     */
    private boolean setRoomInfo(String conferenceId, String sessionId) {
        if (conferenceId == null || sessionId == null) {
            return false;
        }

        boolean result = false;

        RoomManager roomManager = RoomManager.getInstance();
        if (roomManager.hasSession(conferenceId,sessionId)) {
            logger.warn("[{}] Already existed in room [{}]", sessionId, conferenceId);
        }
        else {
            result = roomManager.addSession(conferenceId, sessionId);
            logger.debug("[{}] Room addSession [{}] result [{}]", conferenceId, sessionId, result);
        }

        return result;
    }
}

