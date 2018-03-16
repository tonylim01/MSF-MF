package com.uangel.acs.rmqif.handler.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.uangel.acs.AppInstance;
import com.uangel.acs.config.AmfConfig;
import com.uangel.acs.rmqif.module.RmqClient;
import com.uangel.acs.rmqif.types.RmqHeader;
import com.uangel.acs.rmqif.types.RmqMessage;
import com.uangel.acs.rmqif.types.RmqMessageType;
import com.uangel.core.rabbitmq.message.RmqBuilder;
import com.uangel.core.rabbitmq.transport.RmqCallback;
import com.uangel.core.rabbitmq.transport.RmqSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class RmqOutgoingMessage implements RmqOutgoingMessageInterface {

    private static final Logger logger = LoggerFactory.getLogger(RmqOutgoingMessage.class);

    public static final int RMQ_TARGET_ID_MCUD  = 1;

    private RmqHeader header;
    private JsonElement jsonElement = null;

    public RmqOutgoingMessage() {
        this.header = new RmqHeader();
    }

    public RmqOutgoingMessage(String sessionId, long transactionId) {
        this.header = new RmqHeader();

        setSessionId(sessionId);
        setTransactionId(transactionId);
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
    public void setBody(Object obj, Type objType) {
        Gson gson = new GsonBuilder().create();
        jsonElement = gson.toJsonTree(obj, objType);
    }

    @Override
    public boolean sendTo(int targetId) {
        boolean result = false;

        AmfConfig config = AppInstance.getInstance().getConfig();
        if (config == null) {
            return false;
        }

        String target = null;
        if (targetId == RMQ_TARGET_ID_MCUD) {
            target = config.getMcudName();
        }

        RmqMessage msg = new RmqMessage(header);
        if (jsonElement != null) {
            msg.setBody(jsonElement);
        }

        try {
            String json = RmqBuilder.build(msg);

            if (json != null) {
                RmqClient client = RmqClient.getInstance(target);
                if (client != null) {
                    result = client.send(json);

                    if (result) {
                        logger.info("[{}] -> ({}) {}", msg.getSessionId(), target,
                                RmqMessageType.getMessageTypeStr(msg.getMessageType()));
                    }
                    else {
                        logger.error("[{}] -> ({}) {} failed", msg.getSessionId(), target,
                                RmqMessageType.getMessageTypeStr(msg.getMessageType()));
                    }

                    return result;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
