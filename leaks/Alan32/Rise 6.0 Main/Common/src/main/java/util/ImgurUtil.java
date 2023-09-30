package util;


import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public final class ImgurUtil {
    public static final String UPLOAD_API_URL = "https://api.imgur.com/3/image";
    public static final String ALBUM_API_URL = "https://api.imgur.com/3/album";
    public static final int MAX_UPLOAD_ATTEMPTS = 3;

    //CHANGE TO @CLIENT_ID@ and replace with buildscript.
    private final static String CLIENT_ID = "efce6070269a7f1";

    /**
     * Takes a file and uploads it to Imgur.
     * Does not check to see if the file is an image, this should be done
     * before the file is passed to this method.
     *
     * @param file The image to be uploaded to Imgur.
     * @return The JSON response from Imgur.
     */
    public static String upload(final File file) {
        final HttpURLConnection conn = getHttpConnection(UPLOAD_API_URL);
        writeToConnection(conn, "image=" + toBase64(file));
        return getResponse(conn);
    }

    public static String upload(final byte[] file) {
        final HttpURLConnection conn = getHttpConnection(UPLOAD_API_URL);
        writeToConnection(conn, "image=" + toBase64(file));
        return getResponse(conn);
    }

    /**
     * Creates an album on Imgur.
     * Does not check if imageIds are valid images on Imgur.
     *
     * @param imageIds A list of ids of images on Imgur.
     * @return The JSON response from Imgur.
     */
    public static String createAlbum(final List<String> imageIds) {
        final HttpURLConnection conn = getHttpConnection(ALBUM_API_URL);
        String ids = "";
        for (final String id : imageIds) {
            if (!ids.equals("")) {
                ids += ",";
            }
            ids += id;
        }
        writeToConnection(conn, "ids=" + ids);
        return getResponse(conn);
    }

    /**
     * Converts a file to a Base64 String.
     *
     * @param file The file to be converted.
     * @return The file as a Base64 String.
     */
    private static String toBase64(final File file) {
        try {
            final byte[] b = new byte[(int) file.length()];
            final FileInputStream fs = new FileInputStream(file);
            fs.read(b);
            fs.close();
            return URLEncoder.encode(DatatypeConverter.printBase64Binary(b), "UTF-8");
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String toBase64(final byte[] file) {
        try {
            return URLEncoder.encode(DatatypeConverter.printBase64Binary(file), "UTF-8");
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Creates and sets up an HttpURLConnection for use with the Imgur API.
     *
     * @param url The URL to connect to. (check Imgur API for correct URL).
     * @return The newly created HttpURLConnection.
     */
    private static HttpURLConnection getHttpConnection(final String url) {
        final HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Client-ID " + CLIENT_ID);
            conn.setReadTimeout(100000);
            conn.connect();
            return conn;
        } catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Sends the provided message to the connection as uploaded data.
     *
     * @param conn    The connection to send the data to.
     * @param message The data to upload.
     */
    private static void writeToConnection(final HttpURLConnection conn, final String message) {
        final OutputStreamWriter writer;
        try {
            writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(message);
            writer.flush();
            writer.close();
        } catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Gets the response from the connection, Usually in the format of a JSON string.
     *
     * @param conn The connection to listen to.
     * @return The response, usually as a JSON string.
     */
    private static String getResponse(final HttpURLConnection conn) {
        final StringBuilder str = new StringBuilder();
        final BufferedReader reader;
        try {
            if (conn.getResponseCode() != 200) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                str.append(line);
            }
            reader.close();
        } catch (final IOException e) {
            return null;
        }
        if (str.toString().equals("")) {
            return null;
        }
        return str.toString();
    }
}
