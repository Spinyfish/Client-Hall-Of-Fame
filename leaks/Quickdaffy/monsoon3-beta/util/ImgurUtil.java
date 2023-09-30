/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.DatatypeConverter
 */
package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import javax.xml.bind.DatatypeConverter;

public final class ImgurUtil {
    public static final String UPLOAD_API_URL = "https://api.imgur.com/3/image";
    public static final String ALBUM_API_URL = "https://api.imgur.com/3/album";
    public static final int MAX_UPLOAD_ATTEMPTS = 3;
    private static final String CLIENT_ID = "efce6070269a7f1";

    public static String upload(File file) {
        HttpURLConnection conn = ImgurUtil.getHttpConnection(UPLOAD_API_URL);
        ImgurUtil.writeToConnection(conn, "image=" + ImgurUtil.toBase64(file));
        return ImgurUtil.getResponse(conn);
    }

    public static String upload(byte[] file) {
        HttpURLConnection conn = ImgurUtil.getHttpConnection(UPLOAD_API_URL);
        ImgurUtil.writeToConnection(conn, "image=" + ImgurUtil.toBase64(file));
        return ImgurUtil.getResponse(conn);
    }

    public static String createAlbum(List<String> imageIds) {
        HttpURLConnection conn = ImgurUtil.getHttpConnection(ALBUM_API_URL);
        String ids = "";
        for (String id : imageIds) {
            if (!ids.equals("")) {
                ids = ids + ",";
            }
            ids = ids + id;
        }
        ImgurUtil.writeToConnection(conn, "ids=" + ids);
        return ImgurUtil.getResponse(conn);
    }

    private static String toBase64(File file) {
        try {
            byte[] b = new byte[(int)file.length()];
            FileInputStream fs = new FileInputStream(file);
            fs.read(b);
            fs.close();
            return URLEncoder.encode(DatatypeConverter.printBase64Binary((byte[])b), "UTF-8");
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String toBase64(byte[] file) {
        try {
            return URLEncoder.encode(DatatypeConverter.printBase64Binary((byte[])file), "UTF-8");
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static HttpURLConnection getHttpConnection(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Client-ID efce6070269a7f1");
            conn.setReadTimeout(100000);
            conn.connect();
            return conn;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private static void writeToConnection(HttpURLConnection conn, String message) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(message);
            writer.flush();
            writer.close();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private static String getResponse(HttpURLConnection conn) {
        StringBuilder str = new StringBuilder();
        try {
            String line;
            if (conn.getResponseCode() != 200) {
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                str.append(line);
            }
            reader.close();
        }
        catch (IOException e) {
            return null;
        }
        if (str.toString().equals("")) {
            return null;
        }
        return str.toString();
    }
}

