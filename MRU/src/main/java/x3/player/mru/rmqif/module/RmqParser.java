package x3.player.mru.rmqif.module;

import com.google.gson.Gson;
import x3.player.mru.rmqif.types.RmqMessage;

public class RmqParser {

    public static RmqMessage parse(String json) throws Exception {

        Gson gson = new Gson();
        RmqMessage msg = gson.fromJson(json, RmqMessage.class);

        return msg;
    }


}
