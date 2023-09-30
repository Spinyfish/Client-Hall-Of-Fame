package club.pulsive.client.intent.yay.web;

import club.pulsive.client.intent.yay.EnvironmentConstants;
import club.pulsive.client.intent.yay.util.ConstructableEntry;
import lombok.experimental.UtilityClass;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;

@UtilityClass
public class Browser implements EnvironmentConstants {

    public String getResponse(String getParameters) throws IOException {
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
    public String postResponse(String getParameters, ConstructableEntry<String, String>... post) throws IOException {
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

}
