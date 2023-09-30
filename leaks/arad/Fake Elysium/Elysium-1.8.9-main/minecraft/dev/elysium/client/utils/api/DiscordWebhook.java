package dev.elysium.client.utils.api;

import com.google.gson.JsonObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DiscordWebhook {
    public static boolean send(String urls, String username, String content) {
        try {
        	
        	//who token loggy
            // will be used for crash report
        	
            HttpURLConnection connection;
            URL url = new URL(urls);
            JsonObject json = new JsonObject();
            json.addProperty("content", content);
            json.addProperty("username", username);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("User-Agent", "Minecraft-Client");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoOutput(true);
            OutputStream stream = connection.getOutputStream();
            stream.write(json.toString().getBytes());
            stream.flush();
            stream.close();

            int status = connection.getResponseCode();

            connection.getInputStream().close();
            connection.disconnect();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
