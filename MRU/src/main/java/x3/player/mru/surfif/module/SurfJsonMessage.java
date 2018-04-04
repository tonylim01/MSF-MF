package x3.player.mru.surfif.module;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import x3.player.mru.common.JsonMessage;

public class SurfJsonMessage<T> {

    private JsonMessage<T> jsonMessage = null;

    public SurfJsonMessage(Class<T> classType) {
        jsonMessage = new JsonMessage<>(classType);
    }

    public String build(String msgName, T data) {
        Gson gson = new Gson();
        JsonElement jsonData = gson.toJsonTree(data);
        JsonObject obj = new JsonObject();
        obj.add(msgName, jsonData);
        return obj.toString();
    }

}
