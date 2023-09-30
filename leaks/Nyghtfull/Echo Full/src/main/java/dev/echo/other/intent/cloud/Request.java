package dev.echo.other.intent.cloud;

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
        String apiKey = //Echo.RELEASE.equals(ReleaseType.DEV) ? "BBsb8XT5k4PQ" :
                Cloud.getApiKey();
        getMap.put("key", apiKey);
        getMap.put("product_code", Cloud.getProductCode());

        JsonObject response = Cloud.request(url, getMap, postMap);
        if (response != null && response.has("response")) {
            return response.isJsonObject() ? response.getAsJsonObject() : response;
        }

        return null;
    }

}
