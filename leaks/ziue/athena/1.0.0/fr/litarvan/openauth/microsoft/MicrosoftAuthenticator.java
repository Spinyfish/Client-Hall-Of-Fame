package fr.litarvan.openauth.microsoft;

import java.util.concurrent.*;
import java.util.*;
import fr.litarvan.openauth.microsoft.model.response.*;
import fr.litarvan.openauth.microsoft.model.request.*;
import java.net.*;
import java.io.*;
import java.util.regex.*;

public class MicrosoftAuthenticator
{
    public static final String MICROSOFT_AUTHORIZATION_ENDPOINT = "https://login.microsoftonline.com/consumers/oauth2/v2.0/authorize";
    public static final String MICROSOFT_TOKEN_ENDPOINT = "https://login.live.com/oauth20_token.srf";
    public static final String MICROSOFT_REDIRECTION_ENDPOINT = "https://login.live.com/oauth20_desktop.srf";
    public static final String XBOX_LIVE_AUTH_HOST = "user.auth.xboxlive.com";
    public static final String XBOX_LIVE_CLIENT_ID = "000000004C12AE6F";
    public static final String XBOX_LIVE_SERVICE_SCOPE = "service::user.auth.xboxlive.com::MBI_SSL";
    public static final String XBOX_LIVE_AUTHORIZATION_ENDPOINT = "https://user.auth.xboxlive.com/user/authenticate";
    public static final String XSTS_AUTHORIZATION_ENDPOINT = "https://xsts.auth.xboxlive.com/xsts/authorize";
    public static final String MINECRAFT_AUTH_ENDPOINT = "https://api.minecraftservices.com/authentication/login_with_xbox";
    public static final String XBOX_LIVE_AUTH_RELAY = "http://auth.xboxlive.com";
    public static final String MINECRAFT_AUTH_RELAY = "rp://api.minecraftservices.com/";
    public static final String MINECRAFT_STORE_ENDPOINT = "https://api.minecraftservices.com/entitlements/mcstore";
    public static final String MINECRAFT_PROFILE_ENDPOINT = "https://api.minecraftservices.com/minecraft/profile";
    public static final String MINECRAFT_STORE_IDENTIFIER = "game_minecraft";
    private final HttpClient http;
    
    public MicrosoftAuthenticator() {
        this.http = new HttpClient();
    }
    
    public MicrosoftAuthResult loginWithCredentials(final String email, final String password) throws MicrosoftAuthenticationException {
        final CookieHandler currentHandler = CookieHandler.getDefault();
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        final Map<String, String> params = new HashMap<String, String>();
        params.put("login", email);
        params.put("loginfmt", email);
        params.put("passwd", password);
        HttpURLConnection result;
        try {
            final PreAuthData authData = this.preAuthRequest();
            params.put("PPFT", authData.getPPFT());
            result = this.http.followRedirects(this.http.postForm(authData.getUrlPost(), params));
        }
        finally {
            CookieHandler.setDefault(currentHandler);
        }
        try {
            return this.loginWithTokens(this.extractTokens(result.getURL().toString()), true);
        }
        catch (MicrosoftAuthenticationException e) {
            if (this.match("(identity/confirm)", this.http.readResponse(result)) != null) {
                throw new MicrosoftAuthenticationException("User has enabled double-authentication or must allow sign-in on https://account.live.com/activity");
            }
            throw e;
        }
    }
    
    public MicrosoftAuthResult loginWithWebview() throws MicrosoftAuthenticationException {
        try {
            return this.loginWithAsyncWebview().get();
        }
        catch (InterruptedException | ExecutionException ex2) {
            final Exception ex;
            final Exception e = ex;
            throw new MicrosoftAuthenticationException(e);
        }
    }
    
    public CompletableFuture<MicrosoftAuthResult> loginWithAsyncWebview() {
        if (!System.getProperty("java.version").startsWith("1.")) {
            CookieHandler.setDefault(new CookieManager());
        }
        final String url = String.format("%s?%s", "https://login.microsoftonline.com/consumers/oauth2/v2.0/authorize", this.http.buildParams(this.getLoginParams()));
        final LoginFrame frame = new LoginFrame();
        return frame.start(url).thenApplyAsync(result -> {
            try {
                if (result != null) {
                    return this.loginWithTokens(this.extractTokens(result), true);
                }
                else {
                    return null;
                }
            }
            catch (MicrosoftAuthenticationException e) {
                throw new CompletionException(e);
            }
        });
    }
    
    public MicrosoftAuthResult loginWithRefreshToken(final String refreshToken) throws MicrosoftAuthenticationException {
        final Map<String, String> params = this.getLoginParams();
        params.put("refresh_token", refreshToken);
        params.put("grant_type", "refresh_token");
        final MicrosoftRefreshResponse response = this.http.postFormGetJson("https://login.live.com/oauth20_token.srf", params, MicrosoftRefreshResponse.class);
        return this.loginWithTokens(new AuthTokens(response.getAccessToken(), response.getRefreshToken()), true);
    }
    
    public MicrosoftAuthResult loginWithTokens(final AuthTokens tokens) throws MicrosoftAuthenticationException {
        return this.loginWithTokens(tokens, true);
    }
    
    public MicrosoftAuthResult loginWithTokens(final AuthTokens tokens, final boolean retrieveProfile) throws MicrosoftAuthenticationException {
        final XboxLoginResponse xboxLiveResponse = this.xboxLiveLogin(tokens.getAccessToken());
        final XboxLoginResponse xstsResponse = this.xstsLogin(xboxLiveResponse.getToken());
        final String userHash = xstsResponse.getDisplayClaims().getUsers()[0].getUserHash();
        final MinecraftLoginResponse minecraftResponse = this.minecraftLogin(userHash, xstsResponse.getToken());
        final MinecraftStoreResponse storeResponse = this.http.getJson("https://api.minecraftservices.com/entitlements/mcstore", minecraftResponse.getAccessToken(), MinecraftStoreResponse.class);
        if (Arrays.stream(storeResponse.getItems()).noneMatch(item -> item.getName().equals("game_minecraft"))) {
            throw new MicrosoftAuthenticationException("Player didn't buy Minecraft Java Edition or did not migrate its account");
        }
        MinecraftProfile profile = null;
        if (retrieveProfile) {
            profile = this.http.getJson("https://api.minecraftservices.com/minecraft/profile", minecraftResponse.getAccessToken(), MinecraftProfile.class);
        }
        return new MicrosoftAuthResult(profile, minecraftResponse.getAccessToken(), tokens.getRefreshToken(), xboxLiveResponse.getDisplayClaims().getUsers()[0].getUserHash(), Base64.getEncoder().encodeToString(minecraftResponse.getUsername().getBytes()));
    }
    
    protected PreAuthData preAuthRequest() throws MicrosoftAuthenticationException {
        final Map<String, String> params = this.getLoginParams();
        params.put("display", "touch");
        params.put("locale", "en");
        final String result = this.http.getText("https://login.microsoftonline.com/consumers/oauth2/v2.0/authorize", params);
        final String ppft = this.match("sFTTag:'.*value=\"([^\"]*)\"", result);
        final String urlPost = this.match("urlPost: ?'(.+?(?='))", result);
        return new PreAuthData(ppft, urlPost);
    }
    
    protected XboxLoginResponse xboxLiveLogin(final String accessToken) throws MicrosoftAuthenticationException {
        final XboxLiveLoginProperties properties = new XboxLiveLoginProperties("RPS", "user.auth.xboxlive.com", accessToken);
        final XboxLoginRequest<XboxLiveLoginProperties> request = new XboxLoginRequest<XboxLiveLoginProperties>(properties, "http://auth.xboxlive.com", "JWT");
        return this.http.postJson("https://user.auth.xboxlive.com/user/authenticate", request, XboxLoginResponse.class);
    }
    
    protected XboxLoginResponse xstsLogin(final String xboxLiveToken) throws MicrosoftAuthenticationException {
        final XSTSAuthorizationProperties properties = new XSTSAuthorizationProperties("RETAIL", new String[] { xboxLiveToken });
        final XboxLoginRequest<XSTSAuthorizationProperties> request = new XboxLoginRequest<XSTSAuthorizationProperties>(properties, "rp://api.minecraftservices.com/", "JWT");
        return this.http.postJson("https://xsts.auth.xboxlive.com/xsts/authorize", request, XboxLoginResponse.class);
    }
    
    protected MinecraftLoginResponse minecraftLogin(final String userHash, final String xstsToken) throws MicrosoftAuthenticationException {
        final MinecraftLoginRequest request = new MinecraftLoginRequest(String.format("XBL3.0 x=%s;%s", userHash, xstsToken));
        return this.http.postJson("https://api.minecraftservices.com/authentication/login_with_xbox", request, MinecraftLoginResponse.class);
    }
    
    protected Map<String, String> getLoginParams() {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("client_id", "000000004C12AE6F");
        params.put("redirect_uri", "https://login.live.com/oauth20_desktop.srf");
        params.put("scope", "service::user.auth.xboxlive.com::MBI_SSL");
        params.put("response_type", "token");
        return params;
    }
    
    protected AuthTokens extractTokens(final String url) throws MicrosoftAuthenticationException {
        return new AuthTokens(this.extractValue(url, "access_token"), this.extractValue(url, "refresh_token"));
    }
    
    protected String extractValue(final String url, final String key) throws MicrosoftAuthenticationException {
        final String matched = this.match(key + "=([^&]*)", url);
        if (matched == null) {
            throw new MicrosoftAuthenticationException("Invalid credentials or tokens");
        }
        try {
            return URLDecoder.decode(matched, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new MicrosoftAuthenticationException(e);
        }
    }
    
    protected String match(final String regex, final String content) {
        final Matcher matcher = Pattern.compile(regex).matcher(content);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);
    }
}
