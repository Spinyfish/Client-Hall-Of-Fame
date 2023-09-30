package dev.echo.other.intent.cloud;

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
        url += "?" + get.entrySet().stream().map(entry -> {
            try {
                return URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()) + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.joining("&"));

        try {
            HttpsURLConnection conn = (HttpsURLConnection) new URL("https://intent.store/api/configuration/" + url).openConnection();
            conn.setRequestProperty("User-Agent", "Intent-API/1.0 TenacityBot");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            StringJoiner sj = new StringJoiner("&");
            for (Map.Entry<String, String> entry : post.entrySet()) {
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            conn.setFixedLengthStreamingMode(length);
            conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.connect();

            OutputStream os = conn.getOutputStream();
            os.write(out);

            InputStream stream = conn.getResponseCode() / 100 == 2 ? conn.getInputStream() : conn.getErrorStream();
            if (stream == null) return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            JsonElement element;
            element = JsonParser.parseReader(reader);

            if (element.isJsonObject()) {
                return element.getAsJsonObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
