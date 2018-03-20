package x3.player.mru.rmqif.module;

import com.google.gson.Gson;
import x3.player.mru.rmqif.types.RmqMessage;

public class RmqBuilder {

    public static String build(RmqMessage msg) throws Exception {

        Gson gson = new Gson();
        String json = gson.toJson(msg);

        return json;
    }

}
