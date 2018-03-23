package x3.player.mru.rmqif.module;

import com.google.gson.Gson;
import x3.player.mru.rmqif.types.RmqMessage;

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
