/* November.lol © 2023 */
package lol.november.feature.account.microshit;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lol.november.feature.account.cookie.Cookie;
import lol.november.feature.account.cookie.CookieParser;
import lol.november.feature.account.microshit.data.MinecraftProfile;
import lol.november.feature.account.microshit.data.OAuthData;
import lol.november.feature.account.microshit.data.XboxLoginData;
import lol.november.feature.account.microshit.exception.MicroshitException;
import lol.november.utility.fs.FileUtils;
import lol.november.utility.net.RequestUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

/**
 * @author Gavin
 * @since 2.0.0
 * <a href="https://mojang-api-docs.gapple.pw/authentication/msa">documentation used</a>
 */
public class MicroshitAuthenticator {

  private static final Minecraft mc = Minecraft.getMinecraft();

  private static final String USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:107.0) Gecko/20100101 Firefox/107.0";

  private static final String OAUTH_AUTH_URL =
    "https://login.live.com/oauth20_authorize.srf?client_id=000000004C12AE6F&redirect_uri=https://login.live.com/oauth20_desktop.srf&scope=service::user.auth.xboxlive.com::MBI_SSL&display=touch&response_type=token&locale=en";
  private static final String XBOX_LIVE_AUTH_URL =
    "https://user.auth.xboxlive.com/user/authenticate";
  private static final String XSTS_AUTH_URL =
    "https://xsts.auth.xboxlive.com/xsts/authorize";
  private static final String LOGIN_WITH_XBOX_URL =
    "https://api.minecraftservices.com/authentication/login_with_xbox";
  private static final String PROFILE_URL =
    "https://api.minecraftservices.com/minecraft/profile";

  private static final Pattern SFTT_TAG_PATTERN = Pattern.compile(
    "value=\"(.+?)\""
  );
  private static final Pattern POST_URL_PATTERN = Pattern.compile(
    "urlPost:'(.+?)'"
  );

  /**
   * Logs into a microshit account with a username and password
   *
   * @param cookie the cookie file contents
   * @throws IOException
   * @throws MicroshitException
   */
  public void login(String cookie) throws MicroshitException, IOException {
    MinecraftProfile profile = getProfileData(cookie);
    if (profile != null) mc.session =
      new Session(
        profile.username(),
        profile.id(),
        profile.accessToken(),
        "msa"
      );
  }

  /**
   * Logs into a microshit account with a username and password
   *
   * @param email    the email
   * @param password the password
   * @throws IOException
   * @throws MicroshitException
   */
  public void login(String email, String password)
    throws MicroshitException, IOException {
    MinecraftProfile profile = getProfileData(email, password);
    if (profile != null) mc.session =
      new Session(
        profile.username(),
        profile.id(),
        profile.accessToken(),
        "msa"
      );
  }

  /**
   * Gets the profile data for an email and a password
   *
   * @param cookie the cookie file content data
   * @return the MinecraftProfile for this email and password
   * @throws IOException
   * @throws MicroshitException
   */
  public MinecraftProfile getProfileData(String cookie)
    throws IOException, MicroshitException {
    Map<String, String> loginData = getLoginData(cookie);
    if (
      loginData == null || !loginData.containsKey("access_token")
    ) throw new MicroshitException("Access token cannot be null");

    // i want to kill myself
    XboxLoginData xboxLogin = loginToXbox(loginData.get("access_token"));
    if (xboxLogin == null) throw new MicroshitException(
      "Xbox Login data should not be null"
    );

    XboxLoginData xstsData = getXSTSData(xboxLogin);
    if (xstsData == null) throw new MicroshitException(
      "XSTS data should not be null"
    );

    String accessToken = authWithXbox(xstsData);
    return getProfile(
      accessToken,
      loginData.getOrDefault("refresh_token", null)
    );
  }

  /**
   * Gets the profile data for an email and a password
   *
   * @param email    the email
   * @param password the password
   * @return the MinecraftProfile for this email and password
   * @throws IOException
   * @throws MicroshitException
   */
  public MinecraftProfile getProfileData(String email, String password)
    throws IOException, MicroshitException {
    OAuthData oauthData = getOAuthData();
    if (oauthData == null) throw new NullPointerException(
      "OAuthData should not be null"
    );

    Map<String, String> loginData = getLoginData(oauthData, email, password);
    if (
      loginData == null || !loginData.containsKey("access_token")
    ) throw new MicroshitException("Access token cannot be null");

    // i want to kill myself
    XboxLoginData xboxLogin = loginToXbox(loginData.get("access_token"));
    if (xboxLogin == null) throw new MicroshitException(
      "Xbox Login data should not be null"
    );

    XboxLoginData xstsData = getXSTSData(xboxLogin);
    if (xstsData == null) throw new MicroshitException(
      "XSTS data should not be null"
    );

    String accessToken = authWithXbox(xstsData);
    return getProfile(
      accessToken,
      loginData.getOrDefault("refresh_token", null)
    );
  }

  /**
   * Gets the profile on the access token and refresh token
   *
   * @param accessToken  the access token (required)
   * @param refreshToken the refresh token (optional)
   * @return the MinecraftProfile object or null if the request fails
   * @throws IOException if reading the result of the request fails
   */
  public MinecraftProfile getProfile(String accessToken, String refreshToken)
    throws IOException {
    Map<String, String> headers = new HashMap<>();
    headers.put("Accept", "application/json");
    headers.put("Authorization", "Bearer " + accessToken);

    String data = RequestUtils.get(PROFILE_URL, headers, false);
    if (data == null) return null;

    JsonObject obj = FileUtils.jsonParser.parse(data).getAsJsonObject();

    return new MinecraftProfile(
      obj.get("name").getAsString(),
      obj.get("id").getAsString(),
      accessToken,
      refreshToken
    );
  }

  /**
   * (finally holy fuck) Authenticates into minecraft with xbox
   *
   * @param data the login data
   * @return the access token needed for future requests
   */
  private String authWithXbox(XboxLoginData data) {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put("Accept", "application/json");

    String content = RequestUtils.post(
      LOGIN_WITH_XBOX_URL,
      headers,
      false,
      "{\"identityToken\":\"XBL3.0 x=" +
      data.uhs() +
      ";" +
      data.token() +
      "\",\"ensureLegacyEnabled\":true}"
    );
    return content == null
      ? null
      : FileUtils.jsonParser
        .parse(content)
        .getAsJsonObject()
        .get("access_token")
        .getAsString();
  }

  /**
   * Logs into the xbox account with the provided access token
   *
   * @param accessToken the access token
   * @return the XboxLoginData object with the Token and UHS, or null
   */
  private XboxLoginData loginToXbox(String accessToken) {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put("Accept", "application/json");

    String body =
      "{\"Properties\":{\"AuthMethod\":\"RPS\",\"SiteName\":\"user.auth.xboxlive.com\",\"RpsTicket\":\"" +
      accessToken +
      "\"},\"RelyingParty\":\"http://auth.xboxlive.com\",\"TokenType\":\"JWT\"}";
    String data = RequestUtils.post(XBOX_LIVE_AUTH_URL, headers, false, body);
    if (data == null) return null;

    JsonObject obj = FileUtils.jsonParser.parse(data).getAsJsonObject();

    String token = obj.get("Token").getAsString();
    String uhs = obj
      .get("DisplayClaims")
      .getAsJsonObject()
      .get("xui")
      .getAsJsonArray()
      .get(0)
      .getAsJsonObject()
      .get("uhs")
      .getAsString(); // wtf

    return new XboxLoginData(token, uhs);
  }

  /**
   * Gets the XSTS data needed
   *
   * @param data the previous XboxLoginData object from the xbox login request
   * @return the resulting updated XboxLoginData or null if request fails
   * @throws IOException        if the output or input streams fail to write/read
   * @throws MicroshitException if the account is locked in some way from authorizing, or no result was found
   */
  private XboxLoginData getXSTSData(XboxLoginData data)
    throws IOException, MicroshitException {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put("Accept", "application/json");

    HttpURLConnection connection = RequestUtils.post(
      XSTS_AUTH_URL,
      headers,
      "{\"Properties\":{\"SandboxId\":\"RETAIL\",\"UserTokens\":[\"" +
      data.token() +
      "\"]},\"RelyingParty\":\"rp://api.minecraftservices.com/\",\"TokenType\":\"JWT\"})"
    );

    if (connection == null) return null;

    String content = RequestUtils.readConnection(connection);
    if (content == null) throw new MicroshitException(
      "input stream cannot be null"
    );

    JsonObject jsonObject = FileUtils.jsonParser
      .parse(content)
      .getAsJsonObject();

    if (connection.getResponseCode() == 401) {
      long errCode = jsonObject.get("XErr").getAsLong();
      if (errCode == 2148916238L) {
        throw new MicroshitException(
          "account is owned by user under the age of 18"
        );
      } else if (errCode == 2148916233L) {
        throw new MicroshitException(
          "account does not have an xbox account created. please create one"
        );
      }

      return null;
    }

    return new XboxLoginData(jsonObject.get("Token").getAsString(), data.uhs());
  }

  /**
   * Gets the user data from the oauth data
   *
   * @param cookieContent the cookie file content
   * @return the map that contains the access_token, refresh_token, expires_at, etc
   * @throws IOException        if the output or input streams fail to write/read
   * @throws MicroshitException if the authentication with Microshit fails
   */
  private Map<String, String> getLoginData(String cookieContent)
    throws IOException, MicroshitException {
    Map<String, String> headers = new HashMap<>();
    headers.put(
      "Accept",
      "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8"
    );
    headers.put("Connection", "keep-alive");
    headers.put("Accept-Language", "en-US,en;q=0.5");
    headers.put("Accept-Encoding", "gzip, deflate, br");
    headers.put("User-Agent", USER_AGENT);

    Map<String, Cookie> cookieMap = CookieParser.parse(cookieContent);
    StringJoiner joiner = new StringJoiner("; ");
    for (Cookie cookie : cookieMap.values()) {
      joiner.add(cookie.name() + "=" + cookie.value());
    }
    headers.put("Cookie", joiner.toString());

    HttpURLConnection connection = RequestUtils.get(OAUTH_AUTH_URL, headers);
    if (connection == null) return null;

    String data = RequestUtils.readConnection(connection);
    if (data == null) return null;

    String redirectedUrl = connection.getURL().toString();
    if (redirectedUrl.equals(OAUTH_AUTH_URL)) throw new MicroshitException(
      "Login failed, [url=" + redirectedUrl + "]"
    );

    if (data.contains("Sign in to")) throw new MicroshitException(
      "Invalid credentials"
    );

    if (
      data.contains("Help us protect your account")
    ) throw new MicroshitException("2FA has been enabled on this account");

    String raw = connection.getURL().toString().split("#")[1];

    Map<String, String> loginData = new LinkedHashMap<>();
    for (String param : raw.split("&")) {
      String[] paramSplit = param.split("=");
      loginData.put(paramSplit[0], paramSplit[1]);
    }

    return loginData;
  }

  /**
   * Gets the user data from the oauth data
   *
   * @param oauthData the oauth data result
   * @param email     the email
   * @param password  the password
   * @return the map that contains the access_token, refresh_token, expires_at, etc
   * @throws IOException        if the output or input streams fail to write/read
   * @throws MicroshitException if the authentication with Microshit fails
   */
  private Map<String, String> getLoginData(
    OAuthData oauthData,
    String email,
    String password
  ) throws IOException, MicroshitException {
    Map<String, String> headers = new HashMap<>();
    headers.put("Cookie", oauthData.cookie());
    headers.put("Content-Type", "application/x-www-form-urlencoded");

    email = URLEncoder.encode(email, StandardCharsets.UTF_8);
    password = URLEncoder.encode(password, StandardCharsets.UTF_8);
    HttpURLConnection connection = RequestUtils.post(
      oauthData.postUrl(),
      headers,
      "login=" +
      email +
      "&loginfmt=" +
      email +
      "&passwd=" +
      password +
      "&PPFT=" +
      oauthData.sfttTag()
    );
    if (connection == null) return null;

    String data = RequestUtils.readConnection(connection);
    if (data == null) return null;

    String redirectedUrl = connection.getURL().toString();
    if (
      redirectedUrl.contains("accessToken") &&
      redirectedUrl.equals(oauthData.postUrl())
    ) throw new MicroshitException("Login failed, [url=" + redirectedUrl + "]");

    if (data.contains("Sign in to")) throw new MicroshitException(
      "Invalid credentials"
    );

    if (
      data.contains("Help us protect your account")
    ) throw new MicroshitException("2FA has been enabled on this account");

    String raw = connection.getURL().toString().split("#")[1];

    Map<String, String> loginData = new LinkedHashMap<>();
    for (String param : raw.split("&")) {
      String[] paramSplit = param.split("=");
      loginData.put(paramSplit[0], paramSplit[1]);
    }

    return loginData;
  }

  /**
   * Gets the needed oauth data needed to log in
   *
   * @return the OAuthData object
   * @throws IOException if the stream fails to be read from
   */
  public OAuthData getOAuthData() throws IOException {
    Map<String, String> headers = new HashMap<>();
    headers.put("User-Agent", USER_AGENT);
    headers.put(
      "Accept",
      "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8"
    );

    HttpURLConnection connection = RequestUtils.get(OAUTH_AUTH_URL, headers);
    if (connection == null) return null;

    String data = RequestUtils.readConnection(connection);
    if (data == null) return null;

    String sfttTag = null, postUrl = null;

    Matcher matcher = SFTT_TAG_PATTERN.matcher(data);
    if (matcher.find()) {
      sfttTag = matcher.group(1);
    }

    if ((matcher = POST_URL_PATTERN.matcher(data)).find()) {
      postUrl = matcher.group(1);
    }

    // u need this or else microsoft will rape u
    List<String> cookieData = connection.getHeaderFields().get("Set-Cookie");
    String cookie = String.join(";", cookieData);

    return new OAuthData(sfttTag, postUrl, cookie);
  }
}
