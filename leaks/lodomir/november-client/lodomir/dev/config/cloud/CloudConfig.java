/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.config.cloud;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import lodomir.dev.config.ConfigHandler;

public class CloudConfig {
    private static final ConfigHandler cfg = new ConfigHandler();

    public static void loadConfig(String name) {
        try {
            String current;
            HttpsURLConnection connection = (HttpsURLConnection)new URL("https://raw.githubusercontent.com/Lodomireq/november-cloud/configs" + name).openConnection();
            connection.setConnectTimeout(5000);
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            ArrayList<String> response = new ArrayList<String>();
            while ((current = in.readLine()) != null) {
                response.add(current);
            }
            cfg.loadFromList(response);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static List<String> getConfigs() {
        try {
            String current;
            HttpsURLConnection connection = (HttpsURLConnection)new URL("https://raw.githubusercontent.comhttps://github.com/Lodomireq/november-cloud/main/index").openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            ArrayList<String> response = new ArrayList<String>();
            while ((current = in.readLine()) != null) {
                response.add(current);
            }
            return response;
        }
        catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }
}

