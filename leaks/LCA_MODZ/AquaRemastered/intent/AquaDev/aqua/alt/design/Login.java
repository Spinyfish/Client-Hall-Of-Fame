package intent.AquaDev.aqua.alt.design;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.exceptions.AuthenticationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.util.Session;

public class Login {
   private static final String clientId = "00000000402b5328";
   private static final String scopeUrl = "service::user.auth.xboxlive.com::MBI_SSL";
   private static final String servicesApi = "https://api.minecraftservices.com";
   private static String loginUrl = null;
   private static String loginCookie = null;
   private static String loginPPFT = null;

   public static Session logIn(String email, String password) throws IOException, URISyntaxException, AuthenticationException {
      Login.MicrosoftToken microsoftToken = generateTokenPair(generateLoginCode(email, password));
      Login.XboxLiveToken xboxLiveToken = generateXboxTokenPair(microsoftToken);
      Login.XboxToken xboxToken = generateXboxTokenPair(xboxLiveToken);
      URL url = new URL("https://api.minecraftservices.com/authentication/login_with_xbox");
      URLConnection urlConnection = url.openConnection();
      HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;
      httpURLConnection.setRequestMethod("POST");
      httpURLConnection.setDoOutput(true);
      JsonObject request = new JsonObject();
      request.add("identityToken", new JsonPrimitive("XBL3.0 x=" + xboxToken.uhs + ";" + xboxToken.token));
      String requestBody = request.toString();
      httpURLConnection.setFixedLengthStreamingMode(requestBody.length());
      httpURLConnection.setRequestProperty("Content-Type", "application/json");
      httpURLConnection.setRequestProperty("Host", new URI("https://api.minecraftservices.com").getPath());
      httpURLConnection.connect();
      httpURLConnection.getOutputStream().write(requestBody.getBytes(StandardCharsets.US_ASCII));
      JsonObject jsonObject = parseResponseData(httpURLConnection);
      Login.MinecraftProfile minecraftProfile = checkOwnership(jsonObject.get("access_token").getAsString());
      return new Session(minecraftProfile.username, minecraftProfile.uuid.toString(), jsonObject.get("access_token").getAsString(), "LEGACY");
   }

   public static Login.MinecraftProfile checkOwnership(String minecraftToken) throws AuthenticationException {
      try {
         URL url = new URL("https://api.minecraftservices.com/minecraft/profile");
         URLConnection urlConnection = url.openConnection();
         HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;
         httpURLConnection.setRequestMethod("GET");
         httpURLConnection.setRequestProperty("Authorization", "Bearer " + minecraftToken);
         httpURLConnection.setRequestProperty("Host", new URI("https://api.minecraftservices.com").getPath());
         httpURLConnection.connect();
         JsonObject jsonObject = parseResponseData(httpURLConnection);
         UUID uuid = generateUUID(jsonObject.get("id").getAsString());
         String name = jsonObject.get("name").getAsString();
         return new Login.MinecraftProfile(uuid, name);
      } catch (IOException var7) {
         throw new AuthenticationException(String.format("Authentication error. Request could not be made! Cause: '%s'", var7.getMessage()));
      } catch (URISyntaxException | AuthenticationException var8) {
         throw new RuntimeException(var8);
      }
   }

   private static UUID generateUUID(String trimmedUUID) {
      StringBuilder builder = new StringBuilder(trimmedUUID.trim());
      builder.insert(20, "-");
      builder.insert(16, "-");
      builder.insert(12, "-");
      builder.insert(8, "-");
      return UUID.fromString(builder.toString());
   }

   public static String generateLoginCode(String email, String password) throws AuthenticationException {
      try {
         URL url = new URL(
            "https://login.live.com/oauth20_authorize.srf?redirect_uri=https://login.live.com/oauth20_desktop.srf&scope=service::user.auth.xboxlive.com::MBI_SSL&display=touch&response_type=code&locale=en&client_id=00000000402b5328"
         );
         HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
         InputStream inputStream = httpURLConnection.getResponseCode() == 200 ? httpURLConnection.getInputStream() : httpURLConnection.getErrorStream();
         loginCookie = httpURLConnection.getHeaderField("set-cookie");
         String responseData = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());
         Matcher bodyMatcher = Pattern.compile("sFTTag:[ ]?'.*value=\"(.*)\"/>'").matcher(responseData);
         if (!bodyMatcher.find()) {
            throw new AuthenticationException("Authentication error. Could not find 'LOGIN-PFTT' tag from response!");
         }

         loginPPFT = bodyMatcher.group(1);
         bodyMatcher = Pattern.compile("urlPost:[ ]?'(.+?(?='))").matcher(responseData);
         if (!bodyMatcher.find()) {
            throw new AuthenticationException("Authentication error. Could not find 'LOGIN-URL' tag from response!");
         }

         loginUrl = bodyMatcher.group(1);
         if (loginCookie == null || loginPPFT == null || loginUrl == null) {
            throw new AuthenticationException("Authentication error. Error in authentication process!");
         }
      } catch (IOException var7) {
         throw new AuthenticationException(String.format("Authentication error. Request could not be made! Cause: '%s'", var7.getMessage()));
      }

      return sendCodeData(email, password);
   }

   private static String sendCodeData(String email, String password) throws AuthenticationException {
      HashMap<String, String> requestData = new HashMap<>();
      requestData.put("login", email);
      requestData.put("loginfmt", email);
      requestData.put("passwd", password);
      requestData.put("PPFT", loginPPFT);
      String postData = encodeURL(requestData);

      String authToken;
      try {
         byte[] data = postData.getBytes(StandardCharsets.UTF_8);
         HttpURLConnection connection = (HttpURLConnection)new URL(loginUrl).openConnection();
         connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
         connection.setRequestProperty("Content-Length", String.valueOf(data.length));
         connection.setRequestProperty("Cookie", loginCookie);
         connection.setDoInput(true);
         connection.setDoOutput(true);
         connection.getOutputStream().write(data);
         if (connection.getResponseCode() != 200 || connection.getURL().toString().equalsIgnoreCase(loginUrl)) {
            throw new AuthenticationException("Authentication error. Username or password is not valid.");
         }

         Pattern pattern = Pattern.compile("[?|&]code=([\\w.-]+)");
         Matcher tokenMatcher = pattern.matcher(URLDecoder.decode(connection.getURL().toString(), StandardCharsets.UTF_8.name()));
         if (!tokenMatcher.find()) {
            throw new AuthenticationException("Authentication error. Could not handle data from response.");
         }

         authToken = tokenMatcher.group(1);
      } catch (IOException var9) {
         throw new AuthenticationException(String.format("Authentication error. Request could not be made! Cause: '%s'", var9.getMessage()));
      }

      loginUrl = null;
      loginCookie = null;
      loginPPFT = null;
      return authToken;
   }

   private static void sendXboxRequest(HttpURLConnection httpURLConnection, JsonObject request, JsonObject properties) throws IOException {
      request.add("Properties", properties);
      String requestBody = request.toString();
      httpURLConnection.setFixedLengthStreamingMode(requestBody.length());
      httpURLConnection.setRequestProperty("Content-Type", "application/json");
      httpURLConnection.setRequestProperty("Accept", "application/json");
      httpURLConnection.connect();
      httpURLConnection.getOutputStream().write(requestBody.getBytes(StandardCharsets.US_ASCII));
   }

   public static Login.MicrosoftToken generateTokenPair(String authToken) throws AuthenticationException {
      try {
         HashMap<String, String> arguments = new HashMap<>();
         arguments.put("client_id", "00000000402b5328");
         arguments.put("code", authToken);
         arguments.put("grant_type", "authorization_code");
         arguments.put("redirect_uri", "https://login.live.com/oauth20_desktop.srf");
         arguments.put("scope", "service::user.auth.xboxlive.com::MBI_SSL");
         StringJoiner argumentBuilder = new StringJoiner("&");
         arguments.forEach((key, value) -> argumentBuilder.add(encodeURL(key) + "=" + encodeURL(value)));
         byte[] data = argumentBuilder.toString().getBytes(StandardCharsets.UTF_8);
         URL url = new URL("https://login.live.com/oauth20_token.srf");
         URLConnection urlConnection = url.openConnection();
         HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;
         httpURLConnection.setRequestMethod("POST");
         httpURLConnection.setDoOutput(true);
         httpURLConnection.setFixedLengthStreamingMode(data.length);
         httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         httpURLConnection.connect();
         httpURLConnection.getOutputStream().write(data);
         JsonObject jsonObject = parseResponseData(httpURLConnection);
         return new Login.MicrosoftToken(jsonObject.get("access_token").getAsString(), jsonObject.get("refresh_token").getAsString());
      } catch (IOException var8) {
         throw new AuthenticationException(String.format("Authentication error. Request could not be made! Cause: '%s'", var8.getMessage()));
      }
   }

   public static Login.XboxLiveToken generateXboxTokenPair(Login.MicrosoftToken microsoftToken) throws AuthenticationException {
      try {
         URL url = new URL("https://user.auth.xboxlive.com/user/authenticate");
         URLConnection urlConnection = url.openConnection();
         HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;
         httpURLConnection.setDoOutput(true);
         JsonObject request = new JsonObject();
         request.add("RelyingParty", new JsonPrimitive("http://auth.xboxlive.com"));
         request.add("TokenType", new JsonPrimitive("JWT"));
         JsonObject properties = new JsonObject();
         properties.add("AuthMethod", new JsonPrimitive("RPS"));
         properties.add("SiteName", new JsonPrimitive("user.auth.xboxlive.com"));
         properties.add("RpsTicket", new JsonPrimitive(microsoftToken.token));
         sendXboxRequest(httpURLConnection, request, properties);
         JsonObject jsonObject = parseResponseData(httpURLConnection);
         String uhs = jsonObject.get("DisplayClaims").getAsJsonObject().getAsJsonArray("xui").get(0).getAsJsonObject().get("uhs").getAsString();
         return new Login.XboxLiveToken(jsonObject.get("Token").getAsString(), uhs);
      } catch (IOException var8) {
         throw new AuthenticationException(String.format("Authentication error. Request could not be made! Cause: '%s'", var8.getMessage()));
      }
   }

   public static Login.XboxToken generateXboxTokenPair(Login.XboxLiveToken xboxLiveToken) {
      try {
         try {
            URL url = new URL("https://xsts.auth.xboxlive.com/xsts/authorize");
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            JsonObject request = new JsonObject();
            request.add("RelyingParty", new JsonPrimitive("rp://api.minecraftservices.com/"));
            request.add("TokenType", new JsonPrimitive("JWT"));
            JsonObject properties = new JsonObject();
            properties.add("SandboxId", new JsonPrimitive("RETAIL"));
            JsonArray userTokens = new JsonArray();
            userTokens.add(new JsonPrimitive(xboxLiveToken.token));
            properties.add("UserTokens", userTokens);
            sendXboxRequest(httpURLConnection, request, properties);
            if (httpURLConnection.getResponseCode() == 401) {
               throw new AuthenticationException("No xbox account was found!");
            } else {
               JsonObject jsonObject = parseResponseData(httpURLConnection);
               String uhs = jsonObject.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString();
               return new Login.XboxToken(jsonObject.get("Token").getAsString(), uhs);
            }
         } catch (IOException var9) {
            throw new AuthenticationException(String.format("Authentication error. Request could not be made! Cause: '%s'", var9.getMessage()));
         }
      } catch (Throwable var10) {
         throw var10;
      }
   }

   private static JsonObject parseResponseData(HttpURLConnection httpURLConnection) throws IOException, AuthenticationException {
      BufferedReader bufferedReader = httpURLConnection.getResponseCode() != 200
         ? new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()))
         : new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
      String lines = bufferedReader.lines().collect(Collectors.joining());
      JsonObject jsonObject = new GsonBuilder().create().fromJson(lines, JsonObject.class);
      if (jsonObject.has("error")) {
         throw new AuthenticationException(jsonObject.get("error").toString() + ": " + jsonObject.get("error_description"));
      } else {
         return jsonObject;
      }
   }

   private static String encodeURL(String url) {
      try {
         return URLEncoder.encode(url, "UTF-8");
      } catch (UnsupportedEncodingException var2) {
         throw new UnsupportedOperationException(var2);
      }
   }

   private static String encodeURL(HashMap<String, String> map) {
      StringBuilder sb = new StringBuilder();
      map.forEach((key, value) -> {
         if (sb.length() != 0) {
            sb.append("&");
         }

         sb.append(String.format("%s=%s", encodeURL(key), encodeURL(value)));
      });
      return sb.toString();
   }

   public static class MicrosoftToken {
      public final String token;
      public final String refreshToken;

      public MicrosoftToken(String token, String refreshToken) {
         this.token = token;
         this.refreshToken = refreshToken;
      }
   }

   public static class MinecraftProfile {
      public final UUID uuid;
      public final String username;

      public MinecraftProfile(UUID uuid, String username) {
         this.uuid = uuid;
         this.username = username;
      }
   }

   public static class XboxLiveToken {
      public final String token;
      public final String uhs;

      public XboxLiveToken(String token, String uhs) {
         this.token = token;
         this.uhs = uhs;
      }
   }

   public static class XboxToken {
      public final String token;
      public final String uhs;

      public XboxToken(String token, String uhs) {
         this.token = token;
         this.uhs = uhs;
      }
   }
}
