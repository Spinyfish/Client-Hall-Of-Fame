/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package fr.litarvan.openauth.microsoft;

import fr.litarvan.openauth.microsoft.AuthTokens;
import fr.litarvan.openauth.microsoft.HttpClient;
import fr.litarvan.openauth.microsoft.LoginFrame;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.PreAuthData;
import fr.litarvan.openauth.microsoft.model.request.MinecraftLoginRequest;
import fr.litarvan.openauth.microsoft.model.request.XSTSAuthorizationProperties;
import fr.litarvan.openauth.microsoft.model.request.XboxLiveLoginProperties;
import fr.litarvan.openauth.microsoft.model.request.XboxLoginRequest;
import fr.litarvan.openauth.microsoft.model.response.MicrosoftRefreshResponse;
import fr.litarvan.openauth.microsoft.model.response.MinecraftLoginResponse;
import fr.litarvan.openauth.microsoft.model.response.MinecraftProfile;
import fr.litarvan.openauth.microsoft.model.response.MinecraftStoreResponse;
import fr.litarvan.openauth.microsoft.model.response.XboxLoginResponse;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;
import wtf.monsoon.api.util.misc.StringUtil;

public class MicrosoftAuthenticator {
    public static final String MICROSOFT_AUTHORIZATION_ENDPOINT = "https://login.live.com/oauth20_authorize.srf";
    public static final String MICROSOFT_TOKEN_ENDPOINT = "https://login.live.com/oauth20_token.srf";
    public static final String MICROSOFT_REDIRECTION_ENDPOINT = "https://login.live.com/oauth20_desktop.srf";
    public static final String XBOX_LIVE_AUTH_HOST = "user.auth.xboxlive.com";
    public static final String XBOX_LIVE_CLIENT_ID = "000000004C12AE6F";
    public static final String XBOX_LIVE_SERVICE_SCOPE = "service::user.auth.xboxlive.com::MBI_SSL";
    public static final String XBOX_LIVE_AUTHORIZATION_ENDPOINT = "https://user.auth.xboxlive.com/user/authenticate";
    public static final String XSTS_AUTHORIZATION_ENDPOINT = "https://xsts.auth.xboxlive.com/xsts/authorize";
    public static final String MINECRAFT_AUTH_ENDPOINT = "https://api.minecraftservices.com/authentication/login_with_xbox";
    public static final String CHANGE_NAME_ENDPOINT = "https://api.minecraftservices.com/minecraft/profile/name/";
    public static final String XBOX_LIVE_AUTH_RELAY = "http://auth.xboxlive.com";
    public static final String MINECRAFT_AUTH_RELAY = "rp://api.minecraftservices.com/";
    public static final String MINECRAFT_STORE_ENDPOINT = "https://api.minecraftservices.com/entitlements/mcstore";
    public static final String MINECRAFT_PROFILE_ENDPOINT = "https://api.minecraftservices.com/minecraft/profile";
    public static final String MINECRAFT_STORE_IDENTIFIER = "game_minecraft";
    private final HttpClient http = new HttpClient();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MicrosoftAuthResult loginWithCredentials(String email, String password) throws MicrosoftAuthenticationException {
        HttpURLConnection result;
        CookieHandler currentHandler = CookieHandler.getDefault();
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("login", email);
        params.put("loginfmt", email);
        params.put("passwd", password);
        try {
            PreAuthData authData = this.preAuthRequest();
            params.put("PPFT", authData.getPPFT());
            result = this.http.followRedirects(this.http.postForm(authData.getUrlPost(), params));
        }
        finally {
            CookieHandler.setDefault(currentHandler);
        }
        try {
            return this.loginWithTokens(this.extractTokens(result.getURL().toString()));
        }
        catch (MicrosoftAuthenticationException e) {
            if (this.match("identity/confirm", this.http.readResponse(result)) != null) {
                throw new MicrosoftAuthenticationException("User has enabled double-authentication or must allow sign-in on https://account.live.com/activity");
            }
            throw e;
        }
    }

    public MicrosoftAuthResult loginWithWebview() throws MicrosoftAuthenticationException {
        try {
            return this.loginWithAsyncWebview().get();
        }
        catch (InterruptedException | ExecutionException e) {
            throw new MicrosoftAuthenticationException(e);
        }
    }

    public CompletableFuture<MicrosoftAuthResult> loginWithAsyncWebview() {
        String url = String.format("%s?%s", MICROSOFT_AUTHORIZATION_ENDPOINT, this.http.buildParams(this.getLoginParams()));
        LoginFrame frame = new LoginFrame();
        return frame.start(url).thenApplyAsync(result -> {
            try {
                return this.loginWithTokens(this.extractTokens((String)result));
            }
            catch (MicrosoftAuthenticationException e) {
                throw new CompletionException(e);
            }
        });
    }

    public MicrosoftAuthResult loginWithRefreshToken(String refreshToken) throws MicrosoftAuthenticationException {
        Map<String, String> params = this.getLoginParams();
        params.put("refresh_token", refreshToken);
        params.put("grant_type", "refresh_token");
        MicrosoftRefreshResponse response = this.http.postFormGetJson(MICROSOFT_TOKEN_ENDPOINT, params, MicrosoftRefreshResponse.class);
        return this.loginWithTokens(new AuthTokens(response.getAccessToken(), response.getRefreshToken()));
    }

    public MicrosoftAuthResult loginWithTokens(AuthTokens tokens) throws MicrosoftAuthenticationException {
        XboxLoginResponse xboxLiveResponse = this.xboxLiveLogin(tokens.getAccessToken());
        XboxLoginResponse xstsResponse = this.xstsLogin(xboxLiveResponse.getToken());
        String userHash = xstsResponse.getDisplayClaims().getUsers()[0].getUserHash();
        MinecraftLoginResponse minecraftResponse = this.minecraftLogin(userHash, xstsResponse.getToken());
        MinecraftStoreResponse storeResponse = this.http.getJson(MINECRAFT_STORE_ENDPOINT, minecraftResponse.getAccessToken(), MinecraftStoreResponse.class);
        if (Arrays.stream(storeResponse.getItems()).noneMatch(item -> item.getName().equals(MINECRAFT_STORE_IDENTIFIER))) {
            throw new MicrosoftAuthenticationException("Player didn't buy Minecraft Java Edition or did not migrate its account");
        }
        try {
            HttpURLConnection nameConn = this.http.createConnection(MINECRAFT_PROFILE_ENDPOINT);
            nameConn.addRequestProperty("Authorization", "Bearer " + minecraftResponse.getAccessToken());
            nameConn.setDoOutput(true);
            nameConn.addRequestProperty("Content-Type", "application/json");
            nameConn.addRequestProperty("Accept", "*/*");
            nameConn.setRequestMethod("POST");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("profileName", (Object)StringUtil.getValidUsername());
            nameConn.getOutputStream().write(jsonObject.toString().getBytes());
            System.out.println(this.http.readResponse(nameConn));
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        MinecraftProfile profile = this.http.getJson(MINECRAFT_PROFILE_ENDPOINT, minecraftResponse.getAccessToken(), MinecraftProfile.class);
        return new MicrosoftAuthResult(profile, minecraftResponse.getAccessToken(), tokens.getRefreshToken());
    }

    protected PreAuthData preAuthRequest() throws MicrosoftAuthenticationException {
        Map<String, String> params = this.getLoginParams();
        params.put("display", "touch");
        params.put("locale", "en");
        String result = this.http.getText(MICROSOFT_AUTHORIZATION_ENDPOINT, params);
        String ppft = this.match("sFTTag:'.*value=\"([^\"]*)\"", result);
        String urlPost = this.match("urlPost: ?'(.+?(?='))", result);
        return new PreAuthData(ppft, urlPost);
    }

    protected XboxLoginResponse xboxLiveLogin(String accessToken) throws MicrosoftAuthenticationException {
        XboxLiveLoginProperties properties = new XboxLiveLoginProperties("RPS", XBOX_LIVE_AUTH_HOST, accessToken);
        XboxLoginRequest<XboxLiveLoginProperties> request = new XboxLoginRequest<XboxLiveLoginProperties>(properties, XBOX_LIVE_AUTH_RELAY, "JWT");
        return this.http.postJson(XBOX_LIVE_AUTHORIZATION_ENDPOINT, request, XboxLoginResponse.class);
    }

    protected XboxLoginResponse xstsLogin(String xboxLiveToken) throws MicrosoftAuthenticationException {
        XSTSAuthorizationProperties properties = new XSTSAuthorizationProperties("RETAIL", new String[]{xboxLiveToken});
        XboxLoginRequest<XSTSAuthorizationProperties> request = new XboxLoginRequest<XSTSAuthorizationProperties>(properties, MINECRAFT_AUTH_RELAY, "JWT");
        return this.http.postJson(XSTS_AUTHORIZATION_ENDPOINT, request, XboxLoginResponse.class);
    }

    protected MinecraftLoginResponse minecraftLogin(String userHash, String xstsToken) throws MicrosoftAuthenticationException {
        MinecraftLoginRequest request = new MinecraftLoginRequest(String.format("XBL3.0 x=%s;%s", userHash, xstsToken));
        return this.http.postJson(MINECRAFT_AUTH_ENDPOINT, request, MinecraftLoginResponse.class);
    }

    protected Map<String, String> getLoginParams() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("client_id", XBOX_LIVE_CLIENT_ID);
        params.put("redirect_uri", MICROSOFT_REDIRECTION_ENDPOINT);
        params.put("scope", XBOX_LIVE_SERVICE_SCOPE);
        params.put("response_type", "token");
        return params;
    }

    protected AuthTokens extractTokens(String url) throws MicrosoftAuthenticationException {
        return new AuthTokens(this.extractValue(url, "access_token"), this.extractValue(url, "refresh_token"));
    }

    protected String extractValue(String url, String key) throws MicrosoftAuthenticationException {
        String matched = this.match(key + "=([^&]*)", url);
        if (matched == null) {
            throw new MicrosoftAuthenticationException("Invalid credentials or tokens " + key);
        }
        try {
            return URLDecoder.decode(matched, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new MicrosoftAuthenticationException(e);
        }
    }

    protected String match(String regex, String content) {
        Matcher matcher = Pattern.compile(regex).matcher(content);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);
    }
}

