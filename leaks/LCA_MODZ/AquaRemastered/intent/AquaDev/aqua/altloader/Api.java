package intent.AquaDev.aqua.altloader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.HttpsURLConnection;

public class Api {
   public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
   private static final String API_URL = "https://api.easymc.io/v1";
   private static final Gson gson = new Gson();

   public static void redeem(final String token, final Callback<Object> callback) {
      EXECUTOR_SERVICE.execute(
         new Runnable() {
            @Override
            public void run() {
               HttpsURLConnection connection = Api.preparePostRequest(
                  "https://api.easymc.io/v1/token/redeem", "{\"token\":\"" + token + "\",\"client\":\"mod-1.8.9\"}"
               );
               if (connection == null) {
                  callback.done("Could not create Connection. Please try again later.");
               } else {
                  Object o = Api.getResult(connection);
                  if (o instanceof String) {
                     callback.done(o);
                  } else {
                     JsonObject jsonObject = (JsonObject)o;
                     RedeemResponse response = new RedeemResponse();
                     response.session = jsonObject.get("session").getAsString();
                     response.mcName = jsonObject.get("mcName").getAsString();
                     response.uuid = jsonObject.get("uuid").getAsString();
                     callback.done(response);
                  }
               }
            }
         }
      );
   }

   static HttpsURLConnection preparePostRequest(String url, String body) {
      try {
         HttpsURLConnection con = (HttpsURLConnection)new URL(url).openConnection();
         con.setConnectTimeout(10000);
         con.setReadTimeout(10000);
         con.setRequestMethod("POST");
         con.setRequestProperty("Content-Type", "application/json");
         con.setDoOutput(true);
         DataOutputStream wr = new DataOutputStream(con.getOutputStream());
         wr.write(body.getBytes("UTF-8"));
         wr.flush();
         wr.close();
         return con;
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }

   static Object getResult(HttpsURLConnection connection) {
      try {
         InputStreamReader inputStreamReader = connection.getResponseCode() != 200
            ? new InputStreamReader(connection.getErrorStream())
            : new InputStreamReader(connection.getInputStream());
         BufferedReader reader = new BufferedReader(inputStreamReader);
         StringBuilder result = new StringBuilder();

         String line;
         while((line = reader.readLine()) != null) {
            result.append(line);
         }

         reader.close();
         JsonElement jsonElement = gson.fromJson(result.toString(), JsonElement.class);
         if (!jsonElement.isJsonObject()) {
            return "Could not parse response.";
         } else if (jsonElement.getAsJsonObject().has("error")) {
            return jsonElement.getAsJsonObject().get("error").getAsString();
         } else {
            return jsonElement.getAsJsonObject().has("session") && jsonElement.getAsJsonObject().has("uuid") && jsonElement.getAsJsonObject().has("mcName")
               ? jsonElement.getAsJsonObject()
               : "Response is invalid.";
         }
      } catch (Exception var6) {
         var6.printStackTrace();
         return "Error: " + var6.getMessage();
      }
   }
}
