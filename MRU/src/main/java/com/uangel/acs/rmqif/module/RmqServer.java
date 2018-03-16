package com.uangel.acs.rmqif.module;

import com.uangel.acs.AppInstance;
import com.uangel.acs.common.StringValue;
import com.uangel.acs.config.AmfConfig;
import com.uangel.acs.rmqif.handler.RmqProcHangupReq;
import com.uangel.acs.rmqif.handler.RmqProcInboundGetAnswerReq;
import com.uangel.acs.rmqif.handler.RmqProcInboundSetOfferReq;
import com.uangel.acs.rmqif.handler.RmqProcNegoDoneReq;
import com.uangel.acs.rmqif.types.RmqMessage;
import com.uangel.acs.rmqif.types.RmqMessageType;
import com.uangel.core.rabbitmq.message.RmqParser;
import com.uangel.core.rabbitmq.transport.RmqCallback;
import com.uangel.core.rabbitmq.transport.RmqReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqServer {
    private static final Logger logger = LoggerFactory.getLogger(RmqServer.class);

    private RmqReceiver receiver = null;

    public void start() {
        AmfConfig config = AppInstance.getInstance().getConfig();

        logger.info("{} start", getClass().getSimpleName());

        receiver = new RmqReceiver(config.getRmqHost(), config.getRmqUser(), config.getRmqPass(), config.getLocalName());
        receiver.setCallback(new MessageCallback());

        boolean result = receiver.connect();
        logger.info("{} connect ... [{}]", getClass().getSimpleName(), StringValue.getOkFail(result));

        if (result == false) {
            return;
        }

        result = receiver.start();
        logger.info("{} [{}] start ... [{}]", getClass().getSimpleName(), config.getLocalName(), StringValue.getOkFail(result));
    }

    public void stop() {
        receiver.close();

    }

    private void parseRmqMesage(String json) {

        RmqMessage msg = null;

        try {
            msg = RmqParser.parse(json);

            if (msg.getHeader() != null) {
                logger.debug("Received message: header {}", msg.getHeader().toString());
            }

            if (msg.getBody() != null) {
                logger.debug("Received message: body {}", msg.getBody().toString());
            }

            logger.info("[{}] <- ({}) {}", msg.getSessionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.getMessageTypeStr(msg.getMessageType()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (msg == null) {
            return;
        }

        switch (msg.getMessageType()) {
            case RmqMessageType.RMQ_MSG_TYPE_INBOUND_SET_OFFER_REQ:
                RmqProcInboundSetOfferReq inboudSetOfferReq = new RmqProcInboundSetOfferReq();
                inboudSetOfferReq.handle(msg);
                break;
            case RmqMessageType.RMQ_MSG_TYPE_INBOUND_SET_OFFER_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_INBOUND_GET_ANSWER_REQ:
                RmqProcInboundGetAnswerReq inboundGetAnswerReq= new RmqProcInboundGetAnswerReq();
                inboundGetAnswerReq.handle(msg);
                break;
            case RmqMessageType.RMQ_MSG_TYPE_INBOUND_GET_ANSWER_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_OUTBOUND_GET_OFFER_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_OUTBOUND_GET_OFFER_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_OUTBOUND_SET_ANSWER_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_OUTBOUND_SET_ANSWER_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_HANGUP_REQ:
                RmqProcHangupReq hangupReq= new RmqProcHangupReq();
                hangupReq.handle(msg);
                break;
            case RmqMessageType.RMQ_MSG_TYPE_HANGUP_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_NEGO_DONE_REQ:
                RmqProcNegoDoneReq negoDoneReq= new RmqProcNegoDoneReq();
                negoDoneReq.handle(msg);
                break;
            case RmqMessageType.RMQ_MSG_TYPE_NEGO_DONE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_COMMAND_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_COMMAND_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_LONGCALL_CHECK_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_LONGCALL_CHECK_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_PROMPT_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_PROMPT_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_PROMPT_ACK:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_COLLECT_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_COLLECT_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_COLLECT_ACK:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_RECORD_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_RECORD_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_RECORD_ACK:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_STOP_PLAY_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_STOP_PLAY_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_STOP_RECORD_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_STOP_RECORD_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_CONTROL_FILE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_CONTROL_FILE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_ASR_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_ASR_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_ASR_ACK:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_CREATE_CONFERENCE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_CREATE_CONFERENCE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_DELETE_CONFERENCE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_DELETE_CONFERENCE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_JOIN_CONFERENCE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_JOIN_CONFERENCE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_WITHDRAW_CONFERENCE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_WITHDRAW_CONFERENCE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_UPDATE_CONFERENCE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_UPDATE_CONFERENCE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_STARTRECORD_CONFERENCE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_STARTRECORD_CONFERENCE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_STOPRECORD_CONFERENCE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_STOPRECORD_CONFERENCE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_RECORD_CONFERENCE_RPT:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_RECORD_CONFERENCE_ACK:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_CHANGE_CONFERENCE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_CHANGE_CONFERENCE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_DTMF_CONFERENCE_RPT:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_DTMF_CONFERENCE_ACK:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_CONFERENCE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_CONFERENCE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_CONFERENCE_ACK:
                break;
        }




    }

    private class MessageCallback implements RmqCallback {
        @Override
        public void onReceived(String msg) {
            logger.info("onReceived : {}", msg);

            if (msg != null) {
                parseRmqMesage(msg);
            }
        }
    }
}
