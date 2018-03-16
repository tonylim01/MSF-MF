package com.uangel.acs.rmqif.handler;

import com.uangel.acs.rmqif.handler.base.RmqIncomingMessageInterface;
import com.uangel.acs.rmqif.messages.InboundSetOfferReq;
import com.uangel.acs.rmqif.messages.NegoDoneReq;
import com.uangel.acs.rmqif.types.RmqMessage;
import com.uangel.core.rabbitmq.message.RmqData;
import com.uangel.core.sdp.SdpInfo;
import com.uangel.core.sdp.SdpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcNegoDoneReq implements RmqIncomingMessageInterface {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcNegoDoneReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return false;
        }

        RmqData<NegoDoneReq> data = new RmqData<>(NegoDoneReq.class);
        NegoDoneReq req = data.parse(msg);

        if (req == null) {
            logger.error("NegoDoneReq: parsing failed");
            return false;
        }

        logger.info("[{}] NegoDoneReq: sdp [{}]", msg.getSessionId(), req.getSdp());

        //
        // TODO
        //

        RmqProcNegoDoneRes res = new RmqProcNegoDoneRes(msg.getSessionId(), msg.getHeader().getTransactionId());
        if (res.send() == false) {
            // TODO
        }

        return false;
    }

}

