package com.uangel.acs.rmqif.handler.base;

import java.lang.reflect.Type;

public interface RmqOutgoingMessageInterface {
    void setType(String type);

    void setTransactionId(long transactionId);

    void setSessionId(String sessionId);

    void setMessageFrom(String messageFrom);

    void setTrxType(int trxType);

    void setReasonCode(int reasonCode);

    void setReasonStr(String reasonStr);

    String getSessionId();

    void setBody(Object obj, Type objType);

    boolean sendTo(int targetId);
}
