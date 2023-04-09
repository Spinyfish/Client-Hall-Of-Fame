package niggerlib.mc.auth.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import niggerlib.mc.auth.exception.request.InvalidCredentialsException;
import niggerlib.mc.auth.exception.request.RequestException;
import niggerlib.mc.auth.exception.request.ServiceUnavailableException;
import niggerlib.mc.auth.exception.request.UserMigratedException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.UUID;

/**
 * Utilities for making HTTP requests.
 */
public class HTTP {
    private static final Gson GSON;

    static {
        GSON = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDSerializer()).create();
    }

    private HTTP() {
    }

    /**
     * Makes an HTTP request.
     *
     * @param proxy Proxy to use when making the request.
     * @param url   URL to make the request to.
     * @param input Input to provide in the request.
     * @throws RequestException If an error occurs while making the request.
     */
    public static void makeRequest(Proxy proxy, String url, Object input) throws RequestException {
        makeRequest(proxy, url, input, null);
    }

    /**
     * Makes an HTTP request.
     *
     * @param proxy Proxy to use when making the request.
     * @param url   URL to make the request to.
     * @param input Input to provide in the request.
     * @param clazz Class to provide the response as.
     * @param <T>   Type to provide the response as.
     * @return The response of the request.
     * @throws RequestException If an error occurs while making the request.
     */
    public static <T> T makeRequest(Proxy proxy, String url, Object input, Class<T> clazz) throws RequestException {
        JsonElement response = null;
        try {
            String jsonString = input == null ? performGetRequest(proxy, url) : performPostRequest(proxy, url, GSON.toJson(input), "application/json");
            response = GSON.fromJson(jsonString, JsonElement.class);
        } catch (Exception e) {
            throw new ServiceUnavailableException("Could not make request to '" + url + "'.", e);
        }

        if (response != null) {
            if (response.isJsonObject()) {
                JsonObject object = response.getAsJsonObject();
                if (object.has("error")) {
                    String error = object.get("error").getAsString();
                    String cause = object.has("cause") ? object.get("cause").getAsString() : "";
                    String errorMessage = object.has("errorMessage") ? object.get("errorMessage").getAsString() : "";
                    if (!error.equals("")) {
                        if (error.equals("ForbiddenOperationException")) {
                            if (cause != null && cause.equals("UserMigratedException")) {
                                throw new UserMigratedException(errorMessage);
                            } else {
                                throw new InvalidCredentialsException(errorMessage);
                            }
                        } else {
                            throw new RequestException(errorMessage);
                        }
                    }
                }
            }

            if (clazz != null) {
                return GSON.fromJson(response, clazz);
            }
        }

        return null;
    }

    private static HttpURLConnection createUrlConnection(Proxy proxy, String url) throws IOException {
        if (proxy == null) {
            throw new IllegalArgumentException("Proxy cannot be null.");
        }

        if (url == null) {
            throw new IllegalArgumentException("URL cannot be null.");
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(proxy);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }

    private static String performGetRequest(Proxy proxy, String url) throws IOException {
        if (proxy == null) {
            throw new IllegalArgumentException("Proxy cannot be null.");
        }

        if (url == null) {
            throw new IllegalArgumentException("URL cannot be null.");
        }

        HttpURLConnection connection = createUrlConnection(proxy, url);
        connection.setDoInput(true);

        InputStream in = null;
        try {
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                in = connection.getInputStream();
            } else {
                in = connection.getErrorStream();
            }

            if (in != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }

                return result.toString();
            } else {
                return "";
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static String performPostRequest(Proxy proxy, String url, String post, String type) throws IOException {
        if (proxy == null) {
            throw new IllegalArgumentException("Proxy cannot be null.");
        }

        if (url == null) {
            throw new IllegalArgumentException("URL cannot be null.");
        }

        if (post == null) {
            throw new IllegalArgumentException("Post cannot be null.");
        }

        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }

        byte[] bytes = post.getBytes("UTF-8");

        HttpURLConnection connection = createUrlConnection(proxy, url);
        connection.setRequestProperty("Content-Type", type + "; charset=utf-8");
        connection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
        connection.setDoInput(true);
        connection.setDoOutput(true);

        OutputStream out = null;
        try {
            out = connection.getOutputStream();
            out.write(bytes);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }

        InputStream in = null;
        try {
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                in = connection.getInputStream();
            } else {
                in = connection.getErrorStream();
            }

            if (in != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }

                return result.toString();
            } else {
                return "";
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
