package com.uangel.core.rabbitmq.message;

import com.google.gson.Gson;
import com.uangel.acs.rmqif.types.RmqMessage;

public class RmqBuilder {

    public static String build(RmqMessage msg) throws Exception {

        Gson gson = new Gson();
        String json = gson.toJson(msg);

        return json;
    }

}
