package club.pulsive.client.intent.yay;

import club.pulsive.api.main.Pulsive;
import club.pulsive.client.intent.yay.account.GetUserInfo;
import club.pulsive.client.intent.yay.account.IntentAccount;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Cloud {

    public String apiKey, product = "6af0tufZ", uid, username;
    public boolean loggedIn;

    public Cloud(String apiKey) {
        this.apiKey = apiKey;

        new Thread(() -> {
            try {

                IntentAccount account = new GetUserInfo().getIntentAccount(apiKey);
                if(account != null && account != GetUserInfo.parseFailure && account != GetUserInfo.loginFailure) {
                    uid = Integer.toString(account.client_uid);
                    username = account.username;
                    loggedIn = true;
                }

            }catch(Exception e) { }
        }).start();
    }

    public JsonObject request(String url, Map<String, String> get, Map<String, String> post) throws MalformedURLException, IOException {
        url += (get.isEmpty() ? "" : "?") + get.entrySet().stream().map((e) -> URLEncoder.encode(e.getKey()) + "=" + URLEncoder.encode(e.getValue())).collect(Collectors.joining("&"));

        HttpsURLConnection conn = (HttpsURLConnection) new URL("https://intent.store/api/configuration/" + url).openConnection();
        conn.setRequestProperty("User-Agent", "Intent-API/1.0 " + Pulsive.INSTANCE.getClientInfo().getClientName() + "/"+ Pulsive.INSTANCE.getClientInfo().getClientVersion());
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        StringJoiner sj = new StringJoiner("&");
        for(Map.Entry<String, String> entry : post.entrySet())
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                    + URLEncoder.encode(entry.getValue(), "UTF-8"));
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;
        conn.setFixedLengthStreamingMode(length);
        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        conn.connect();
        try(OutputStream os = conn.getOutputStream()) {
            os.write(out);
        }

        InputStream stream = conn.getResponseCode() / 100 == 2 ? conn.getInputStream() : conn.getErrorStream();

        if(stream == null)
            return new JsonObject();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        JsonReader jsonReader = new JsonReader(reader);
        jsonReader.setLenient(true);
        JsonElement element = new JsonParser().parse(jsonReader);

        if(element.isJsonObject())
            return element.getAsJsonObject();

        return new JsonObject();
    }

    public String postConfig(String name, String desc, String meta, String body) {

        Map<String, String> get = new HashMap<>();
        get.put("key", apiKey);
        get.put("product_code", product);
        get.put("name", name);
        get.put("description", desc);
        get.put("meta", meta);

        Map<String, String> post = new HashMap<>();
        post.put("body", body);

        try {
            JsonObject response = request("post", get, post);

            if(response.has("response") && response.get("response").getAsString().equals("success")) {
                JsonObject configuration = response.get("configuration").getAsJsonObject();

                String uuid = configuration.get("uuid").getAsString();
                String shareCode = configuration.get("share_code").getAsString();

                return shareCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateConfig(String shareCode, String meta, String body) {
        return updateConfig(shareCode, null, meta, body);
    }

    public boolean updateConfig(String shareCode, String name, String meta, String body) {

        Map<String, String> get = new HashMap<>();
        get.put("key", apiKey);

        if(name != null && !name.isEmpty())
            get.put("name", name);

        get.put("product_code", product);
        get.put("share_code", shareCode);
        get.put("description", "");
        get.put("meta", meta);

        Map<String, String> post = new HashMap<>();
        post.put("body", body);

        String text = "";

        try {
            JsonObject response = request("update", get, post);

            text = response.toString();

            if(response.has("response") && response.get("response").getAsString().equals("success")) {
                JsonObject configuration = response.get("configuration").getAsJsonObject();

                String uuid = configuration.get("uuid").getAsString();

                return true;
            }
        } catch (Exception e) {
            System.err.println(text);
            e.printStackTrace();
        }

        return false;
    }

    /**
     * @param meta Only list configs with this meta / or leave empty and get all
     */
    public JsonArray listConfigs(String meta) {

        Map<String, String> get = new HashMap<>();
        get.put("key", apiKey);
        get.put("product_code", product);
        get.put("meta", meta);

        Map<String, String> post = new HashMap<>();

        try {
            JsonObject response = request("list", get, post);

            if(response.has("response") && response.get("response").getAsString().equals("success")) {
                JsonArray configurations = response.get("configurations").getAsJsonArray();

                return configurations;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public JsonObject getConfig(String shareCode) {

        Map<String, String> get = new HashMap<>();
        get.put("key", apiKey);
        get.put("product_code", product);
        get.put("share_code", shareCode);

        Map<String, String> post = new HashMap<>();

        try {
            JsonObject response = request("get", get, post);

            if(response.has("response") && response.get("response").getAsString().equals("success")) {
                JsonObject configuration = response.get("configuration").getAsJsonObject();

                String uuid = configuration.get("uuid").getAsString();
                String name = configuration.get("name").getAsString();
                String meta = configuration.get("meta").getAsString();
                JsonObject body = configuration.get("body").getAsJsonObject();

                return body;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean deleteConfig(String shareCode) {

        Map<String, String> get = new HashMap<>();
        get.put("key", apiKey);
        get.put("product_code", product);
        get.put("share_code", shareCode);

        Map<String, String> post = new HashMap<>();

        try {
            JsonObject response = request("delete", get, post);

            if(response.has("response") && response.get("response").getAsString().equals("success")) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}