/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.storage.utils.other;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

public class HttpUtil {
    public static HttpURLConnection createUrlConnection(URL url) throws IOException {
        Validate.notNull(url);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }

    public static String performGetRequest(URL url, boolean withKey) throws IOException {
        return new HttpUtil().performGetRequestWithoutStatic(url, withKey);
    }

    public static String performGetRequest(URL url) throws IOException {
        return new HttpUtil().performGetRequestWithoutStatic(url, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String performGetRequestWithoutStatic(URL url, boolean withKey) throws IOException {
        String var6;
        Validate.notNull(url);
        HttpURLConnection connection = HttpUtil.createUrlConnection(url);
        InputStream inputStream = null;
        connection.setRequestProperty("user-agent", "Mozilla/5.0 AppIeWebKit");
        if (withKey) {
            connection.setRequestProperty("xf-api-key", "LnM-qSeQqtJlJmJnVt76GhU-SoiolWs9");
        }
        try {
            inputStream = connection.getInputStream();
            String string = IOUtils.toString(inputStream, Charsets.UTF_8);
            return string;
        }
        catch (IOException var10) {
            String result;
            IOUtils.closeQuietly(inputStream);
            inputStream = connection.getErrorStream();
            if (inputStream == null) {
                throw var10;
            }
            var6 = result = IOUtils.toString(inputStream, Charsets.UTF_8);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
        return var6;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            String line;
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 AppIeWebKit");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = in.readLine()) != null) {
                result = result + line;
            }
            return result;
        }
        catch (Exception e) {
            System.out.println("\u53d1\u9001 POST \u8bf7\u6c42\u51fa\u73b0\u5f02\u5e38\uff01" + e);
            e.printStackTrace();
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}
