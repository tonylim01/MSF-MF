package com.uangel.acs.rmqif.handler;

import com.uangel.acs.AppInstance;
import com.uangel.acs.config.AmfConfig;
import com.uangel.acs.rmqif.types.RmqHeader;
import com.uangel.acs.rmqif.types.RmqMessage;
import com.uangel.core.rabbitmq.message.RmqBuilder;
import com.uangel.core.rabbitmq.transport.RmqSender;

public class RmqOutgoingMessage implements RmqOutgoingMessageInterface {

    private RmqHeader header;

    public RmqOutgoingMessage() {
        this.header = new RmqHeader();
    }

    @Override
    public void setType(String type) {
        header.setType(type);
    }

    @Override
    public void setTransactionId(long transactionId) {
        header.setTransactionId(transactionId);
    }

    @Override
    public void setSessionId(String sessionId) {
        header.setSessionId(sessionId);
    }

    @Override
    public void setMessageFrom(String messageFrom) {
        header.setMsgFrom(messageFrom);
    }

    @Override
    public void setTrxType(int trxType) {
        header.setTrxType(trxType);
    }

    @Override
    public void setReasonCode(int reasonCode) {
        header.setReasonCode(reasonCode);
    }

    @Override
    public void setReasonStr(String reasonStr) {

    }

    @Override
    public String getSessionId() {
        return header.getSessionId();
    }

    @Override
    public boolean sendTo(String target) {
        boolean result = false;

        AmfConfig config = AppInstance.getInstance().getConfig();
        if (config == null) {
            return false;
        }

        RmqMessage msg = new RmqMessage(header);

        try {
            String json = RmqBuilder.build(msg);

            if (json != null) {
                RmqSender sender = new RmqSender(config.getRmqHost(), config.getRmqUser(), config.getRmqPass(), target);
                sender.connect();
                result = sender.send(json);
                sender.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
