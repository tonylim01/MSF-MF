package x3.player.mru.rmqif.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.rmqif.handler.base.RmqIncomingMessageHandler;
import x3.player.mru.rmqif.messages.CommandStartReq;
import x3.player.mru.rmqif.messages.FileData;
import x3.player.mru.rmqif.module.RmqData;
import x3.player.mru.rmqif.types.RmqMessage;
import x3.player.mru.rmqif.types.RmqMessageType;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionState;
import x3.player.mru.session.SessionStateManager;

public class RmqProcIncomingCommandReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcIncomingCommandReq.class);

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

        RmqData<CommandStartReq> data = new RmqData<>(CommandStartReq.class);
        CommandStartReq req = data.parse(msg);

        if (req == null) {
            logger.error("[{}] CommandReq: parsing failed", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "PARSING FAILURE");
            return false;
        }

        if (req.getType() == null) {
            logger.error("[{}] CommandReq: null type", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "INVALID TYPE");
            return false;
        }

        FileData file = req.getData();
        if (file == null) {
            logger.error("[{}] CommandReq: no data field", msg.getSessionId());
            sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_FAILURE,
                    "NO DATA");
            return false;
        }

        logger.info("[{}] CommandReq: cmd type [{}] channel [{}] file type [{}] file [{}] def [{}] mix [{}] media [{}]",
                msg.getSessionId(), req.getType(), req.getChannel(),
                file.getPlayType(), file.getPlayFile(), file.getDefVolume(), file.getMixVolume(), file.getMediaType());

        sessionInfo.setFromQueue(msg.getHeader().getMsgFrom());

        file.setChannel(req.getChannel());

        if (req.getType().equals(CommandStartReq.CMD_TYPE_MEDIA_PLAY)) {
            SessionStateManager.getInstance().setState(msg.getSessionId(), SessionState.PLAY_START, file);

            sessionInfo.setVolumeMin(req.getData().getMixVolume());
            sessionInfo.setVolumeMax(req.getData().getDefVolume());
        }
        else if (req.getType().equals(CommandStartReq.CMD_TYPE_MEDIA_STOP)) {
            SessionStateManager.getInstance().setState(msg.getSessionId(), SessionState.PLAY_STOP, file);
        }
        else {
            logger.warn("[{}] CommandReq: Unsupported type [{}]", msg.getSessionId(), req.getType());
        }

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());

        return false;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

        RmqProcOutgoingCommandRes res = new RmqProcOutgoingCommandRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send(queueName) == false) {
            // TODO
        }

    }
}
