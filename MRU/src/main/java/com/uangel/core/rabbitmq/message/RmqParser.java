package com.uangel.core.rabbitmq.message;

import com.google.gson.Gson;
import com.uangel.acs.rmqif.types.RmqMessage;

public class RmqParser {

    public static RmqMessage parse(String json) throws Exception {

        Gson gson = new Gson();
        RmqMessage msg = gson.fromJson(json, RmqMessage.class);

        return msg;
    }


}
