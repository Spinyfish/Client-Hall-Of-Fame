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

public class XBLTokenRequestor {
    public static XBLToken getFor(String token) throws IOException, AuthenticationException {
        try {
            URL url = new URL("https://user.auth.xboxlive.com/user/authenticate");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);

            JSONObject request = new JSONObject();
            request.put("RelyingParty", "http://auth.xboxlive.com");
            request.put("TokenType", "JWT");

            JSONObject props = new JSONObject();
            props.put("AuthMethod", "RPS");
            props.put("SiteName", "user.auth.xboxlive.com");
            props.put("RpsTicket", "d=" + token);

            request.put("Properties", props);

            String body = request.toString();

            http.setFixedLengthStreamingMode(body.length());
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Accept", "application/json");
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
            if (json.keySet().contains("error")) {
                throw new AuthenticationException(json.getString("error") + ": " + json.getString("error_description"));
            }
            String uhs = ((JSONObject) ((JSONObject) json.get("DisplayClaims")).getJSONArray("xui").get(0)).getString("uhs");
            return new XBLToken(json.getString("Token"), uhs);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static XBLToken getForUserPass(String token) throws IOException, AuthenticationException {
        try {
            URL url = new URL("https://user.auth.xboxlive.com/user/authenticate");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setDoOutput(true);

            JSONObject request = new JSONObject();
            request.put("RelyingParty", "http://auth.xboxlive.com");
            request.put("TokenType", "JWT");

            JSONObject props = new JSONObject();
            props.put("AuthMethod", "RPS");
            props.put("SiteName", "user.auth.xboxlive.com");
            props.put("RpsTicket", token);

            request.put("Properties", props);

            String body = request.toString();

            http.setFixedLengthStreamingMode(body.length());
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Accept", "application/json");
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
            if (json.keySet().contains("error")) {
                throw new AuthenticationException(json.getString("error") + ": " + json.getString("error_description"));
            }
            String uhs = ((JSONObject) ((JSONObject) json.get("DisplayClaims")).getJSONArray("xui").get(0)).getString("uhs");
            return new XBLToken(json.getString("Token"), uhs);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    static class XBLToken {
        public String token;
        public String uhs;

        public XBLToken(String t, String u) {
            token = t;
            uhs = u;
        }
    }
}
