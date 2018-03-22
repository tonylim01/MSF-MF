package x3.player.mru.rmqif.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.rmqif.handler.base.RmqOutgoingMessage;
import x3.player.mru.rmqif.messages.HangupRes;
import x3.player.mru.rmqif.messages.HeartbeatReq;
import x3.player.mru.rmqif.types.RmqMessageType;
import x3.player.mru.room.RoomManager;
import x3.player.mru.session.SessionManager;

public class RmqProcHeartbeatReq extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcHangupRes.class);

    public RmqProcHeartbeatReq(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_HEARTBEAT);
    }

    public boolean send(String queueName) {
        HeartbeatReq req = new HeartbeatReq();

        SessionManager sessionManager = SessionManager.getInstance();

        req.setSessionTotal(sessionManager.getTotalCount());
        req.setSessionIdle(sessionManager.getIdleCount());

        RoomManager roomManager = RoomManager.getInstance();

        req.setConferenceChannelTotal(roomManager.getTotalRoomCount());
        req.setConferenceChannelIdle(roomManager.getIdleRoomCount());

        setBody(req, HeartbeatReq.class);

        return sendTo(queueName);
    }
}
