package com.uangel.acs.rmqif.handler;

import com.uangel.acs.AppInstance;
import com.uangel.acs.config.SdpConfig;
import com.uangel.acs.rmqif.handler.base.RmqOutgoingMessage;
import com.uangel.acs.rmqif.messages.HangupRes;
import com.uangel.acs.rmqif.messages.InboundGetAnswerRes;
import com.uangel.acs.rmqif.types.RmqMessageType;
import com.uangel.core.sdp.SdpBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcHangupRes extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcHangupRes.class);

    public RmqProcHangupRes(String sessionId, long transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_HANGUP_RES);
    }

    public boolean send() {

        HangupRes res = new HangupRes();

        res.setConferenceId(null);  // TODO
        res.setParticipantCount(0); // TODO

        setBody(res, HangupRes.class);

        boolean result = sendTo(RMQ_TARGET_ID_MCUD);

        if (result) {
            logger.info("[{}] -> HangupRes", getSessionId());
        }
        else {
            logger.error("[{}] -> HangupRes failed", getSessionId());
        }

        return result;
    }
}
