package com.uangel.acs.rmqif.handler.base;

import com.uangel.acs.rmqif.types.RmqMessage;

public interface RmqIncomingMessageInterface {

    boolean handle(RmqMessage msg);
}
