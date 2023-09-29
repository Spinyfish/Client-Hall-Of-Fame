package net.optifine.http;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpUtils {
    private static String playerItemsUrl = null;
    public static final String SERVER_URL = "http://s.optifine.net";
    public static final String POST_URL = "http://optifine.net";

    public static byte[] get(final String urlStr) throws IOException {
        HttpURLConnection httpurlconnection = null;
        byte[] abyte1;

        try {
            final URL url = new URL(urlStr);
            httpurlconnection = (HttpURLConnection) url.openConnection(Minecraft.getMinecraft().getProxy());
            httpurlconnection.setDoInput(true);
            httpurlconnection.setDoOutput(false);
            httpurlconnection.connect();

            if (httpurlconnection.getResponseCode() / 100 != 2) {
                if (httpurlconnection.getErrorStream() != null) {
                    Config.readAll(httpurlconnection.getErrorStream());
                }

                throw new IOException("HTTP response: " + httpurlconnection.getResponseCode());
            }

            final InputStream inputstream = httpurlconnection.getInputStream();
            final byte[] abyte = new byte[httpurlconnection.getContentLength()];
            int i = 0;

            while (true) {
                final int j = inputstream.read(abyte, i, abyte.length - i);

                if (j < 0) {
                    throw new IOException("Input stream closed: " + urlStr);
                }

                i += j;

                if (i >= abyte.length) {
                    break;
                }
            }

            abyte1 = abyte;
        } finally {
            if (httpurlconnection != null) {
                httpurlconnection.disconnect();
            }
        }

        return abyte1;
    }

    public static String post(final String urlStr, final Map headers, final byte[] content) throws IOException {
        HttpURLConnection httpurlconnection = null;
        String s3;

        try {
            final URL url = new URL(urlStr);
            httpurlconnection = (HttpURLConnection) url.openConnection(Minecraft.getMinecraft().getProxy());
            httpurlconnection.setRequestMethod("POST");

            if (headers != null) {
                for (final Object e : headers.keySet()) {
                    final String s = (String) e;
                    final String s1 = "" + headers.get(s);
                    httpurlconnection.setRequestProperty(s, s1);
                }
            }

            httpurlconnection.setRequestProperty("Content-Type", "text/plain");
            httpurlconnection.setRequestProperty("Content-Length", "" + content.length);
            httpurlconnection.setRequestProperty("Content-Language", "en-US");
            httpurlconnection.setUseCaches(false);
            httpurlconnection.setDoInput(true);
            httpurlconnection.setDoOutput(true);
            final OutputStream outputstream = httpurlconnection.getOutputStream();
            outputstream.write(content);
            outputstream.flush();
            outputstream.close();
            final InputStream inputstream = httpurlconnection.getInputStream();
            final InputStreamReader inputstreamreader = new InputStreamReader(inputstream, StandardCharsets.US_ASCII);
            final BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
            final StringBuffer stringbuffer = new StringBuffer();
            String s2;

            while ((s2 = bufferedreader.readLine()) != null) {
                stringbuffer.append(s2);
                stringbuffer.append('\r');
            }

            bufferedreader.close();
            s3 = stringbuffer.toString();
        } finally {
            if (httpurlconnection != null) {
                httpurlconnection.disconnect();
            }
        }

        return s3;
    }

    public static synchronized String getPlayerItemsUrl() {
        if (playerItemsUrl == null) {
            try {
                final boolean flag = Config.parseBoolean(System.getProperty("player.models.local"), false);

                if (flag) {
                    final File file1 = Minecraft.getMinecraft().mcDataDir;
                    final File file2 = new File(file1, "playermodels");
                    playerItemsUrl = file2.toURI().toURL().toExternalForm();
                }
            } catch (final Exception exception) {
                Config.warn("" + exception.getClass().getName() + ": " + exception.getMessage());
            }

            if (playerItemsUrl == null) {
                playerItemsUrl = "http://s.optifine.net";
            }
        }

        return playerItemsUrl;
    }
}
