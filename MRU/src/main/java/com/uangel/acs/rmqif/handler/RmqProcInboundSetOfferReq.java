package com.uangel.acs.rmqif.handler;

import com.uangel.acs.rmqif.handler.base.RmqIncomingMessageInterface;
import com.uangel.acs.rmqif.messages.InboundSetOfferReq;
import com.uangel.acs.rmqif.types.RmqMessage;
import com.uangel.core.rabbitmq.message.RmqData;
import com.uangel.core.sdp.SdpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcInboundSetOfferReq implements RmqIncomingMessageInterface {

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

        logger.info("[{}] <- InboundSetOfferReq: from [{}] to [{}] cnfid [{}]",
                req.getFromNo(), req.getToNo(), req.getConferenceId());

        SdpParser sdpParser = new SdpParser();
        try {
            sdpParser.parse(req.getSdp());
        } catch (Exception e) {
            e.printStackTrace();
        }

        RmqProcInboundSetOfferRes res = new RmqProcInboundSetOfferRes(msg.getHeader().getSessionId(), msg.getHeader().getTransactionId());
        if (res.send() == false) {
            logger.error("[{}] -> InboundSetOfferRes failed", msg.getHeader().getSessionId());
        }

        return false;
    }

}

