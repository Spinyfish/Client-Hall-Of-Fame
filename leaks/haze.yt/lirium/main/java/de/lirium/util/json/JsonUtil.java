package de.lirium.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class JsonUtil {
    public static JsonObject getEasyMCContent(final String url, final String token) throws IOException {
        final RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("client", "mod-1.8.9").build();

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36 Edg/88.0.705.74")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();

        final OkHttpClient httpClient = new OkHttpClient();
        final Response response = httpClient.newCall(request).execute();

        final JsonParser jsonParser = new JsonParser();
        final JsonElement jsonElement = jsonParser.parse(response.body().string());
        return jsonElement.getAsJsonObject();
    }


    public static JsonObject getJson(final String url) throws IOException {
        final URL url1 = new URL(url);
        final HttpsURLConnection urlConnection = (HttpsURLConnection) url1.openConnection();

        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36 Edg/88.0.705.74");
        urlConnection.connect();

        final JsonParser jsonParser = new JsonParser();
        final JsonElement jsonElement = jsonParser.parse(new InputStreamReader((InputStream) urlConnection.getContent()));
        return jsonElement.getAsJsonObject();
    }

}
