package com.uangel.acs.rmqif.handler;

import com.uangel.acs.AppInstance;
import com.uangel.acs.config.AmfConfig;
import com.uangel.acs.config.SdpConfig;
import com.uangel.acs.rmqif.handler.base.RmqOutgoingMessage;
import com.uangel.acs.rmqif.messages.InboundGetAnswerRes;
import com.uangel.acs.rmqif.types.RmqMessageType;
import com.uangel.core.sdp.SdpBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcInboundGetAnswerRes extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcInboundGetAnswerRes.class);

    public RmqProcInboundGetAnswerRes(String sessionId, long transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_INBOUND_GET_ANSWER_RES);
    }

    public boolean send() {

        SdpConfig config = AppInstance.getInstance().getConfig().getSdpConfig();

        SdpBuilder builder = new SdpBuilder();
        builder.setHost(config.getLocalHost());
        builder.setLocalIpAddress(config.getLocalIpAddress());
        builder.setSessionName("acs-mru");      // TODO

        for (String attr: config.getAttributes()) {
            builder.addGeneralAttribute(attr);
        }

        InboundGetAnswerRes res = new InboundGetAnswerRes();
        res.setSdp(builder.build());

        setBody(res, InboundGetAnswerRes.class);

        boolean result = sendTo(RMQ_TARGET_ID_MCUD);

        if (result) {
            logger.info("[{}] -> IncomingGetAnswerRes", getSessionId());
        }
        else {
            logger.error("[{}] -> IncomingGetAnswerRes failed", getSessionId());
        }

        return result;
    }
}
