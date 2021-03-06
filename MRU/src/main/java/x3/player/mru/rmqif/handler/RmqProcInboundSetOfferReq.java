package x3.player.mru.rmqif.handler;

import x3.player.mru.rmqif.handler.base.RmqIncomingMessageHandler;
import x3.player.mru.rmqif.messages.InboundSetOfferReq;
import x3.player.mru.rmqif.types.RmqMessage;
import x3.player.mru.rmqif.types.RmqMessageType;
import x3.player.mru.room.RoomInfo;
import x3.player.mru.room.RoomManager;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionManager;
import x3.player.mru.rmqif.module.RmqData;
import x3.player.core.sdp.SdpInfo;
import x3.player.core.sdp.SdpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.session.SessionState;
import x3.player.mru.session.SessionStateManager;
import x3.player.mru.simulator.BiUdpRelayManager;
import x3.player.mru.surfif.module.SurfChannelManager;

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
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_WRONG_PARAM,
                    "NO SESSION ID");
            return  false;
        }

        if (req.getConferenceId() == null) {
            logger.warn("[{}] InboundSetOfferReq: No conferenceId", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_WRONG_PARAM,
                    "NO CONFERENCE ID");
            return false;
        }

        // sdpInfo can be null for a no-sdp case
        SdpInfo sdpInfo = SdpParser.parseSdp(req.getSdp());

        int parCount;
        parCount = setRoomInfo(req.getConferenceId(), msg.getSessionId());
        if (parCount == 0) {
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "ROOM-SESSION ERROR");
            return false;
        }

        // Creates a sessionInfo and set things following with the offerReq
        SessionManager sessionManager = SessionManager.getInstance();
        SessionInfo sessionInfo = sessionManager.createSession(msg.getSessionId());

        if (sessionInfo == null) {
            logger.warn("[{}] Cannot create session", msg.getSessionId());
            RoomManager.getInstance().removeSession(req.getConferenceId(), msg.getSessionId());

            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "SESSION ERROR");
            return false;
        }

        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo(req.getConferenceId());

        if (roomInfo != null && roomInfo.getGroupId() < 0) {
            int groupId = SurfChannelManager.getInstance().getIdleChannelGroup();
            if (groupId < 0) {
                logger.error("({}) Cannot find idle channel group", msg.getSessionId());

                RoomManager.getInstance().removeSession(req.getConferenceId(), msg.getSessionId());

                sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                        RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                        "NO RESOURCE");
                return false;
            }
            roomInfo.setGroupId(groupId);
        }

        sessionInfo.setSdpInfo(sdpInfo);
        sessionInfo.setConferenceId(req.getConferenceId());
        sessionInfo.setFromNo(req.getFromNo());
        sessionInfo.setToNo(req.getToNo());
        sessionInfo.setCaller((parCount == 1) ? true : false);

        BiUdpRelayManager udpRelayManager = BiUdpRelayManager.getInstance();
        sessionInfo.setSrcLocalPort(udpRelayManager.getNextLocalPort());
        sessionInfo.setDstLocalPort(udpRelayManager.getNextLocalPort());

        logger.debug("[{}] Local port: src [{}] dst [{{]", msg.getSessionId(),
                sessionInfo.getSrcLocalPort(), sessionInfo.getDstLocalPort());
        //
        // TODO
        //

        SessionStateManager.getInstance().setState(msg.getSessionId(), SessionState.OFFER);

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());

        return true;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

        RmqProcInboundSetOfferRes res = new RmqProcInboundSetOfferRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send(queueName) == false) {
            // TODO
        }
    }

    /**
     * Checks a room with the key is a conferenceId.
     * If a room is not found, creates new room and put the sessionId into the room
     * @param conferenceId
     * @param sessionId
     * @return
     */
    private int setRoomInfo(String conferenceId, String sessionId) {
        if (conferenceId == null || sessionId == null) {
            return 0;
        }

        int result = 0;

        RoomManager roomManager = RoomManager.getInstance();
        if (roomManager.hasSession(conferenceId, sessionId)) {
            logger.warn("[{}] Already existed in room [{}]", sessionId, conferenceId);
        }
        else {
            result = roomManager.addSession(conferenceId, sessionId);
            logger.debug("[{}] Room addSession [{}] size [{}]", conferenceId, sessionId, result);
        }

        return result;
    }
}

