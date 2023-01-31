package intent.AquaDev.aqua.altloader;

import com.google.gson.JsonObject;
import javax.net.ssl.HttpsURLConnection;

final class Api$1 implements Runnable {
   final String val$token;
   final Callback val$callback;

   Api$1(String string, Callback callback) {
      this.val$token = string;
      this.val$callback = callback;
   }

   @Override
   public void run() {
      HttpsURLConnection connection = Api.preparePostRequest(
         "https://api.easymc.io/v1/token/redeem", "{\"token\":\"" + this.val$token + "\",\"client\":\"mod-1.8.9\"}"
      );
      if (connection == null) {
         this.val$callback.done("Could not create Connection. Please try again later.");
      } else {
         Object o = Api.getResult(connection);
         if (o instanceof String) {
            this.val$callback.done(o);
         } else {
            JsonObject jsonObject = (JsonObject)o;
            RedeemResponse response = new RedeemResponse();
            response.session = jsonObject.get("session").getAsString();
            response.mcName = jsonObject.get("mcName").getAsString();
            response.uuid = jsonObject.get("uuid").getAsString();
            this.val$callback.done(response);
         }
      }
   }
}
