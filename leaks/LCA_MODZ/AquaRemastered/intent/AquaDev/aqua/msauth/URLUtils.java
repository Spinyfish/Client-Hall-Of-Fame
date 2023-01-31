package intent.AquaDev.aqua.msauth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class URLUtils {
   private static final Gson gson = new Gson();
   private static final String ACCEPTED_RESPONSE = "application/json";

   public static HttpURLConnection createURLConnection(String url) {
      HttpURLConnection connection = null;

      try {
         connection = (HttpURLConnection)new URL(url).openConnection();
         connection.setRequestProperty("User-Agent", "Mozilla/5.0");
         connection.setRequestProperty("Accept-Language", "en-US");
         connection.setRequestProperty("Accept-Charset", "UTF-8");
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      return connection;
   }

   public static JsonObject readJson(HttpURLConnection connection) {
      return gson.fromJson(readResponse(connection), JsonObject.class);
   }

   public static JsonObject postJson(String url, Object request) {
      HttpURLConnection connection = createURLConnection(url);
      connection.setDoOutput(true);
      connection.addRequestProperty("Content-Type", "application/json");
      connection.addRequestProperty("Accept", "application/json");

      try {
         connection.setRequestMethod("POST");
         connection.getOutputStream().write(gson.toJson(request).getBytes(StandardCharsets.UTF_8));
      } catch (IOException var4) {
         var4.printStackTrace();
      }

      return readJson(connection);
   }

   public static String readResponse(HttpURLConnection connection) {
      String redirection = connection.getHeaderField("Location");
      if (redirection != null) {
         return readResponse(createURLConnection(redirection));
      } else {
         StringBuilder response = new StringBuilder();

         String line;
         try (BufferedReader br = new BufferedReader(
               new InputStreamReader(connection.getResponseCode() >= 400 ? connection.getErrorStream() : connection.getInputStream())
            )) {
            while((line = br.readLine()) != null) {
               response.append(line).append('\n');
            }
         } catch (IOException var16) {
            var16.printStackTrace();
         }

         return response.toString();
      }
   }

   private static String readResponse(BufferedReader br) {
      try {
         StringBuilder sb = new StringBuilder();

         String line;
         while((line = br.readLine()) != null) {
            sb.append(line);
         }

         return sb.toString();
      } catch (IOException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public static JsonObject readJSONFromURL(String url, Map<String, String> headers) {
      try {
         HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
         if (headers != null) {
            for(String header : headers.keySet()) {
               connection.addRequestProperty(header, headers.get(header));
            }
         }

         InputStream is = connection.getResponseCode() != 200 ? connection.getErrorStream() : connection.getInputStream();
         BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
         return gson.fromJson(readResponse(rd), JsonObject.class);
      } catch (IOException var5) {
         var5.printStackTrace();
         return null;
      }
   }
}
