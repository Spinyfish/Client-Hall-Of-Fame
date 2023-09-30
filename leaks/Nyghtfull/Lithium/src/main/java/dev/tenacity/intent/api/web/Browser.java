package dev.tenacity.intent.api.web;

import dev.tenacity.intent.api.EnvironmentConstants;
import dev.tenacity.intent.api.util.ConstructableEntry;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

public class Browser implements EnvironmentConstants {

    public static String getResponse(String getParameters) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE + getParameters).openConnection();
        connection.addRequestProperty("User-Agent", USER_AGENT);

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String lineBuffer;
        StringBuilder response = new StringBuilder();
        while ((lineBuffer = reader.readLine()) != null)
            response.append(lineBuffer);

        return response.toString();
    }

    @SafeVarargs
    public static String postResponse(String getParameters, ConstructableEntry<String, String>... post) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(BASE + getParameters).openConnection();
        connection.addRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : post)
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                    + URLEncoder.encode(entry.getValue(), "UTF-8"));
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;
        connection.setFixedLengthStreamingMode(length);
        connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        connection.connect();
        try (OutputStream os = connection.getOutputStream()) {
            os.write(out);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String lineBuffer;
        StringBuilder response = new StringBuilder();
        while ((lineBuffer = reader.readLine()) != null)
            response.append(lineBuffer);

        return response.toString();
    }

    public static String postExternal(String url, String post, boolean json) {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            byte[] out = post.getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            connection.setFixedLengthStreamingMode(length);
            connection.addRequestProperty("Content-Type", json ? "application/json" : "application/x-www-form-urlencoded; charset=UTF-8");
            connection.addRequestProperty("Accept", "application/json");
            connection.connect();
            try (OutputStream os = connection.getOutputStream()) {
                os.write(out);
            }

            int responseCode = connection.getResponseCode();

            InputStream stream = responseCode / 100 == 2 || responseCode / 100 == 3 ? connection.getInputStream() : connection.getErrorStream();

            if (stream == null) {
                System.err.println(responseCode + ": " + url);
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String lineBuffer;
            StringBuilder response = new StringBuilder();
            while ((lineBuffer = reader.readLine()) != null)
                response.append(lineBuffer);

            reader.close();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getBearerResponse(String url, String bearer) {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36");
            connection.addRequestProperty("Accept", "application/json");
            connection.addRequestProperty("Authorization", "Bearer " + bearer);

            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String lineBuffer;
                StringBuilder response = new StringBuilder();
                while ((lineBuffer = reader.readLine()) != null)
                    response.append(lineBuffer);

                return response.toString();
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                String lineBuffer;
                StringBuilder response = new StringBuilder();
                while ((lineBuffer = reader.readLine()) != null)
                    response.append(lineBuffer);

                return response.toString();
            }
        } catch (Exception e) {
            return null;
        }
    }

}
