package dev.africa.pandaware.impl.microshit.server;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class MinecraftTokenRequestor {
    public static MinecraftToken getFor(XSTSTokenRequestor.XSTSToken xstsToken) throws IOException {
        try {
            URL url = new URL("https://api.minecraftservices.com/authentication/login_with_xbox");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);

            JSONObject request = new JSONObject();
            request.put("identityToken", "XBL3.0 x=" + xstsToken.uhs + ";" + xstsToken.token);

            String body = request.toString();

            http.setFixedLengthStreamingMode(body.length());
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Host", "api.minecraftservices.com");
            http.connect();
            try (OutputStream os = http.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.US_ASCII));
            }

            BufferedReader reader;
            if (http.getResponseCode() != 200) {
                reader = new BufferedReader(new InputStreamReader(http.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            }
            String lines = reader.lines().collect(Collectors.joining());

            JSONObject json = new JSONObject(lines);
            return new MinecraftToken(json.getString("access_token"), json.getString("username"));
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void checkAccount(MinecraftToken minecraftToken) throws AuthenticationException, IOException {
        try {
            URL url = new URL("https://api.minecraftservices.com/entitlements/mcstore");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("GET");

            http.setRequestProperty("Authorization", "Bearer " + minecraftToken.accessToken);
            http.setRequestProperty("Host", "api.minecraftservices.com");
            http.connect();

            BufferedReader reader;
            if (http.getResponseCode() != 200) {
                reader = new BufferedReader(new InputStreamReader(http.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            }
            String lines = reader.lines().collect(Collectors.joining());

            JSONObject json = new JSONObject(lines);
            if (json.getJSONArray("items").length() == 0)
                throw new AuthenticationException("User doesn't own Minecraft!");
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static MinecraftProfile getProfile(MinecraftToken minecraftToken) throws AuthenticationException, IOException {
        try {
            URL url = new URL("https://api.minecraftservices.com/minecraft/profile");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("GET");

            http.setRequestProperty("Authorization", "Bearer " + minecraftToken.accessToken);
            http.setRequestProperty("Host", "api.minecraftservices.com");
            http.connect();

            BufferedReader reader;
            if (http.getResponseCode() != 200) {
                reader = new BufferedReader(new InputStreamReader(http.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            }
            String lines = reader.lines().collect(Collectors.joining());

            JSONObject json = new JSONObject(lines);
            if (json.keySet().contains("error"))
                throw new AuthenticationException("Error retrieving profile: " + json.getString("errorMessage"));

            String skinURL = json.getJSONArray("skins").getJSONObject(0).getString("url");
            return new MinecraftProfile(json.getString("id"), json.getString("name"), skinURL);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    static class MinecraftToken {
        public String accessToken;
        public String username;

        public MinecraftToken(String a, String b) {
            accessToken = a;
            username = b;
        }
    }

    static class MinecraftProfile {
        public String uuid;
        public String name;
        public String skinURL;

        public MinecraftProfile(String a, String b, String c) {
            uuid = a;
            name = b;
            skinURL = c;
        }
    }
}
