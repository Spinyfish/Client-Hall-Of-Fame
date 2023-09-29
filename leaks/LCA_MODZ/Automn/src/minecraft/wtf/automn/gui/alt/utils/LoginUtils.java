package wtf.automn.gui.alt.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class LoginUtils {

  public static Session loginToken(String token) {
    URL url = null;
    try {
      url = new URL("https://api.minecraftservices.com/minecraft/profile/");
    } catch (MalformedURLException e) {
      System.err.println("First Error in LoginUtils.loginToken()");
    }
    HttpURLConnection conn = null;

    try {
      conn = (HttpURLConnection) url.openConnection();
    } catch (IOException e) {
      System.err.println("Second Error in LoginUtils.loginToken()");
    }

    conn.setRequestProperty("Authorization", "Bearer " + token);
    conn.setRequestProperty("Content-Type", "application/json");
    try {
      conn.setRequestMethod("GET");
    } catch (ProtocolException e) {
      System.err.println("Third Error in LoginUtils.loginToken()");
    }


    BufferedReader in = null;
    try {
      in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    } catch (IOException e) {
      System.err.println("Fourth Error in LoginUtils.loginToken()");
    }
    String output;

    StringBuilder response = new StringBuilder();
    while (true) {
      try {
        if ((output = in.readLine()) == null)
          break;
      } catch (IOException e) {
        System.err.println("Fifth Error in LoginUtils.loginToken()");
        return null;
      }
      response.append(output);
    }

    String jsonString = response.toString();
    JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();

    String username = obj.get("name").getAsString();
    String uuid = obj.get("id").getAsString();

    Session s = new Session(username, uuid, token, "null");
    try {
      in.close();
    } catch (IOException e) {
      System.err.println("Sixth Error in LoginUtils.loginToken()");
    }
    return s;
  }

}
