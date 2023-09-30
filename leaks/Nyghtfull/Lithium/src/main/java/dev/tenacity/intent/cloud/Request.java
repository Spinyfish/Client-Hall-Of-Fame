package dev.tenacity.intent.cloud;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Request {

    private final Map<String, String> getMap = new HashMap<>();
    private final Map<String, String> postMap = new HashMap<>();
    private final String url;

    public Request(String url) {
        this.url = url;
    }

    public Request modifyGetMap(Consumer<Map<String, String>> map) {
        map.accept(getMap);
        return this;
    }

    public Request modifyPostMap(Consumer<Map<String, String>> map) {
        map.accept(postMap);
        return this;
    }

    public JsonObject post() {
        return null;
    }

}
