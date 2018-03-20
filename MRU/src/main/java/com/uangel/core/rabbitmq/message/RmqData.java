package com.uangel.core.rabbitmq.message;

import com.google.gson.Gson;
import com.uangel.acs.rmqif.types.RmqMessage;

public class RmqData<T> {

    private Class<T> classType;

    public RmqData(Class<T> classType) {
        this.classType = classType;
    }

    public T parse(RmqMessage rmq) {
        Gson gson = new Gson();
        return gson.fromJson(rmq.getBody(), classType);
    }

    public String build(T data) {
        Gson gson = new Gson();
        return gson.toJson(data);
    }
}
