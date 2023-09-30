package dev.tenacity.intent.cloud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Cloud {

    @Getter
    @Setter
    private static String apiKey;

    @Getter
    private static final String productCode = "K3r30n8A";

    public static Request begin(RequestType requestType) {
        return new Request(requestType.getExtension());
    }

    public static JsonObject request(String url, Map<String, String> get, Map<String, String> post) {
        return null;
    }

}
