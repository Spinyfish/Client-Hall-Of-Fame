package com.alan.clients.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtil {

    public static String requestLine(String url, String requestMethod) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(requestMethod);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            return reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}