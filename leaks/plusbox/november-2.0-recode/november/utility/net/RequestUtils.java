/* November.lol © 2023 */
package lol.november.utility.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author aesthetical
 * @since 04/09/23
 */
public class RequestUtils {

  public static final String FIREFOX_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:107.0) Gecko/20100101 Firefox/107.0";
  public static final String ACCEPT_HTML_ALL =
    "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8";

  private static Proxy PROXY = Proxy.NO_PROXY;

  private static final int READ_TIMEOUT = 15 * 1000;
  private static final int CONNECT_TIMEOUT = 15 * 1000;

  /**
   * Sends a GET request to a URL
   *
   * @param url             the url
   * @param headers         the headers of this get request
   * @param followRedirects if to follow with redirects
   * @return the response in a String
   */
  public static String get(
    String url,
    Map<String, String> headers,
    boolean followRedirects
  ) {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url)
        .openConnection(PROXY);

      connection.setReadTimeout(READ_TIMEOUT);
      connection.setConnectTimeout(CONNECT_TIMEOUT);

      connection.setRequestMethod("GET");
      connection.setInstanceFollowRedirects(followRedirects);

      if (headers != null) {
        headers.forEach(connection::setRequestProperty);
      }

      connection.connect();
      return readInputStreamData(connection.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Sends a GET request to a URL
   *
   * @param url     the url
   * @param headers the headers of this get request
   * @return the response in a String
   */
  public static HttpURLConnection get(String url, Map<String, String> headers) {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url)
        .openConnection(PROXY);

      connection.setReadTimeout(READ_TIMEOUT);
      connection.setConnectTimeout(CONNECT_TIMEOUT);

      connection.setRequestMethod("GET");

      if (headers != null) {
        headers.forEach(connection::setRequestProperty);
      }

      connection.setInstanceFollowRedirects(true);
      connection.setDoOutput(true);

      connection.connect();
      return connection;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Sends a POST request to a URL
   *
   * @param url             the url
   * @param headers         the headers of this get request
   * @param followRedirects if to follow with redirects
   * @param body            the body with this POST request
   * @return the response in a String
   */
  public static String post(
    String url,
    Map<String, String> headers,
    boolean followRedirects,
    String body
  ) {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url)
        .openConnection(PROXY);

      connection.setReadTimeout(READ_TIMEOUT);
      connection.setConnectTimeout(CONNECT_TIMEOUT);

      connection.setRequestMethod("POST");
      connection.setInstanceFollowRedirects(followRedirects);

      if (headers != null) {
        headers.forEach(connection::setRequestProperty);
      }

      connection.setDoOutput(true);

      if (
        connection.getOutputStream() != null && body != null && !body.isEmpty()
      ) {
        writeToOutputStream(connection.getOutputStream(), body);
      }

      connection.connect();

      return readInputStreamData(connection.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Sends a POST request to a URL
   *
   * @param url     the url
   * @param headers the headers of this get request
   * @param body    the body with this POST request
   * @return the url connection
   */
  public static HttpURLConnection post(
    String url,
    Map<String, String> headers,
    String body
  ) {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url)
        .openConnection(PROXY);

      connection.setReadTimeout(READ_TIMEOUT);
      connection.setConnectTimeout(CONNECT_TIMEOUT);

      connection.setRequestMethod("POST");
      connection.setInstanceFollowRedirects(true);

      if (headers != null) {
        headers.forEach(connection::setRequestProperty);
      }

      connection.setDoOutput(true);
      if (
        connection.getOutputStream() != null && body != null && !body.isEmpty()
      ) {
        writeToOutputStream(connection.getOutputStream(), body);
      }

      connection.connect();
      return connection;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Sets the proxy to use on this request
   *
   * @param proxy the proxy to use
   */
  public static void setProxy(Proxy proxy) {
    PROXY = proxy;
  }

  /**
   * Writes to an output stream
   *
   * @param stream the stream
   * @param data   the data to write to the output stream
   * @throws IOException if the writing to the output stream fails
   */
  private static void writeToOutputStream(OutputStream stream, String data)
    throws IOException {
    if (stream != null) {
      byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
      stream.write(bytes, 0, bytes.length);
      stream.close();
    }
  }

  /**
   * Reads the input stream off a connection and returns the string representation of it
   *
   * @param connection the url connection instance
   * @return the data in a string or null if the stream is null
   * @throws IOException if the input stream fails to read
   */
  public static String readConnection(HttpURLConnection connection)
    throws IOException {
    if (connection.getDoInput() && connection.getInputStream() != null) {
      return readInputStreamData(connection.getInputStream());
    }

    return null;
  }

  /**
   * Reads the data from the input stream
   *
   * @param stream the stream
   * @return the data in a string or null if the stream is null
   * @throws IOException if the input stream fails to read
   */
  private static String readInputStreamData(InputStream stream)
    throws IOException {
    if (stream == null) {
      return null;
    }

    StringBuilder builder = new StringBuilder();
    int i;
    while ((i = stream.read()) != -1) {
      builder.append((char) i);
    }

    stream.close();
    return builder.toString();
  }
}
