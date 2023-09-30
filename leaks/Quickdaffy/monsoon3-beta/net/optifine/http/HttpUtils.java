/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;

public class HttpUtils {
    private static String playerItemsUrl = null;
    public static final String SERVER_URL = "http://s.optifine.net";
    public static final String POST_URL = "http://optifine.net";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] get(String urlStr) throws IOException {
        byte[] abyte1;
        HttpURLConnection httpurlconnection = null;
        try {
            int j;
            URL url = new URL(urlStr);
            httpurlconnection = (HttpURLConnection)url.openConnection(Minecraft.getMinecraft().getProxy());
            httpurlconnection.setDoInput(true);
            httpurlconnection.setDoOutput(false);
            httpurlconnection.connect();
            if (httpurlconnection.getResponseCode() / 100 != 2) {
                if (httpurlconnection.getErrorStream() != null) {
                    Config.readAll(httpurlconnection.getErrorStream());
                }
                throw new IOException("HTTP response: " + httpurlconnection.getResponseCode());
            }
            InputStream inputstream = httpurlconnection.getInputStream();
            byte[] abyte = new byte[httpurlconnection.getContentLength()];
            int i = 0;
            do {
                if ((j = inputstream.read(abyte, i, abyte.length - i)) >= 0) continue;
                throw new IOException("Input stream closed: " + urlStr);
            } while ((i += j) < abyte.length);
            abyte1 = abyte;
        }
        finally {
            if (httpurlconnection != null) {
                httpurlconnection.disconnect();
            }
        }
        return abyte1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String post(String urlStr, Map headers, byte[] content) throws IOException {
        String s3;
        HttpURLConnection httpurlconnection = null;
        try {
            String s2;
            URL url = new URL(urlStr);
            httpurlconnection = (HttpURLConnection)url.openConnection(Minecraft.getMinecraft().getProxy());
            httpurlconnection.setRequestMethod("POST");
            if (headers != null) {
                for (Object e : headers.keySet()) {
                    String s = (String)e;
                    String s1 = "" + headers.get(s);
                    httpurlconnection.setRequestProperty(s, s1);
                }
            }
            httpurlconnection.setRequestProperty("Content-Type", "text/plain");
            httpurlconnection.setRequestProperty("Content-Length", "" + content.length);
            httpurlconnection.setRequestProperty("Content-Language", "en-US");
            httpurlconnection.setUseCaches(false);
            httpurlconnection.setDoInput(true);
            httpurlconnection.setDoOutput(true);
            OutputStream outputstream = httpurlconnection.getOutputStream();
            outputstream.write(content);
            outputstream.flush();
            outputstream.close();
            InputStream inputstream = httpurlconnection.getInputStream();
            InputStreamReader inputstreamreader = new InputStreamReader(inputstream, "ASCII");
            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
            StringBuffer stringbuffer = new StringBuffer();
            while ((s2 = bufferedreader.readLine()) != null) {
                stringbuffer.append(s2);
                stringbuffer.append('\r');
            }
            bufferedreader.close();
            s3 = stringbuffer.toString();
        }
        finally {
            if (httpurlconnection != null) {
                httpurlconnection.disconnect();
            }
        }
        return s3;
    }

    public static synchronized String getPlayerItemsUrl() {
        if (playerItemsUrl == null) {
            try {
                boolean flag = Config.parseBoolean(System.getProperty("player.models.local"), false);
                if (flag) {
                    File file1 = Minecraft.getMinecraft().mcDataDir;
                    File file2 = new File(file1, "playermodels");
                    playerItemsUrl = file2.toURI().toURL().toExternalForm();
                }
            }
            catch (Exception exception) {
                Config.warn("" + exception.getClass().getName() + ": " + exception.getMessage());
            }
            if (playerItemsUrl == null) {
                playerItemsUrl = SERVER_URL;
            }
        }
        return playerItemsUrl;
    }
}

