/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import net.minecraft.client.Minecraft;
import optifine.Config;

public class HttpUtils {
    public static final String SERVER_URL = "http://s.optifine.net";
    public static final String POST_URL = "http://optifine.net";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] get(String p_get_0_) throws IOException {
        byte[] abyte1;
        HttpURLConnection httpurlconnection = null;
        try {
            int j;
            URL url = new URL(p_get_0_);
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
                throw new IOException("Input stream closed: " + p_get_0_);
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
    public static String post(String p_post_0_, Map p_post_1_, byte[] p_post_2_) throws IOException {
        String s3;
        HttpURLConnection httpurlconnection = null;
        try {
            String s2;
            URL url = new URL(p_post_0_);
            httpurlconnection = (HttpURLConnection)url.openConnection(Minecraft.getMinecraft().getProxy());
            httpurlconnection.setRequestMethod("POST");
            if (p_post_1_ != null) {
                for (Object s : p_post_1_.keySet()) {
                    String s1 = "" + p_post_1_.get(s);
                    httpurlconnection.setRequestProperty((String)s, s1);
                }
            }
            httpurlconnection.setRequestProperty("Content-Type", "text/plain");
            httpurlconnection.setRequestProperty("Content-Length", "" + p_post_2_.length);
            httpurlconnection.setRequestProperty("Content-Language", "en-US");
            httpurlconnection.setUseCaches(false);
            httpurlconnection.setDoInput(true);
            httpurlconnection.setDoOutput(true);
            OutputStream outputstream = httpurlconnection.getOutputStream();
            outputstream.write(p_post_2_);
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
}

