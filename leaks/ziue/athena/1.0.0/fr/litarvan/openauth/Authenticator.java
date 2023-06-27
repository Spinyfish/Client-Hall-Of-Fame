package fr.litarvan.openauth;

import fr.litarvan.openauth.model.response.*;
import fr.litarvan.openauth.model.request.*;
import com.google.gson.*;
import fr.litarvan.openauth.model.*;
import java.nio.charset.*;
import java.net.*;
import java.io.*;

public class Authenticator
{
    @Deprecated
    public static final String MOJANG_AUTH_URL = "https://authserver.mojang.com/";
    private final String authURL;
    private final AuthPoints authPoints;
    
    public Authenticator(final String authURL, final AuthPoints authPoints) {
        this.authURL = authURL;
        this.authPoints = authPoints;
    }
    
    public AuthResponse authenticate(final AuthAgent agent, final String username, final String password, final String clientToken) throws AuthenticationException {
        return this.authenticate(agent, username, password, clientToken, Proxy.NO_PROXY);
    }
    
    public AuthResponse authenticate(final AuthAgent agent, final String username, final String password, final String clientToken, final Proxy proxy) throws AuthenticationException {
        final AuthRequest request = new AuthRequest(agent, username, password, clientToken);
        return (AuthResponse)this.sendRequest(request, AuthResponse.class, this.authPoints.getAuthenticatePoint(), proxy);
    }
    
    public RefreshResponse refresh(final String accessToken, final String clientToken) throws AuthenticationException {
        return this.refresh(accessToken, clientToken, Proxy.NO_PROXY);
    }
    
    public RefreshResponse refresh(final String accessToken, final String clientToken, final Proxy proxy) throws AuthenticationException {
        final RefreshRequest request = new RefreshRequest(accessToken, clientToken);
        return (RefreshResponse)this.sendRequest(request, RefreshResponse.class, this.authPoints.getRefreshPoint(), proxy);
    }
    
    public void validate(final String accessToken) throws AuthenticationException {
        this.validate(accessToken, Proxy.NO_PROXY);
    }
    
    public void validate(final String accessToken, final Proxy proxy) throws AuthenticationException {
        final ValidateRequest request = new ValidateRequest(accessToken);
        this.sendRequest(request, null, this.authPoints.getValidatePoint(), proxy);
    }
    
    public void signout(final String username, final String password) throws AuthenticationException {
        this.signout(username, password, Proxy.NO_PROXY);
    }
    
    public void signout(final String username, final String password, final Proxy proxy) throws AuthenticationException {
        final SignoutRequest request = new SignoutRequest(username, password);
        this.sendRequest(request, null, this.authPoints.getSignoutPoint(), proxy);
    }
    
    public void invalidate(final String accessToken, final String clientToken) throws AuthenticationException {
        this.invalidate(accessToken, clientToken, Proxy.NO_PROXY);
    }
    
    public void invalidate(final String accessToken, final String clientToken, final Proxy proxy) throws AuthenticationException {
        final InvalidateRequest request = new InvalidateRequest(accessToken, clientToken);
        this.sendRequest(request, null, this.authPoints.getInvalidatePoint(), proxy);
    }
    
    private Object sendRequest(final Object request, final Class<?> model, final String authPoint) throws AuthenticationException {
        return this.sendRequest(request, model, authPoint, Proxy.NO_PROXY);
    }
    
    private Object sendRequest(final Object request, final Class<?> model, final String authPoint, final Proxy proxy) throws AuthenticationException {
        final Gson gson = new Gson();
        String response;
        try {
            response = this.sendPostRequest(this.authURL + authPoint, gson.toJson(request), proxy);
        }
        catch (IOException e) {
            throw new AuthenticationException(new AuthError("Can't send the request : " + e.getClass().getName(), e.getMessage(), "Unknown"));
        }
        if (model != null) {
            return gson.fromJson(response, (Class)model);
        }
        return null;
    }
    
    private String sendPostRequest(final String url, final String json) throws AuthenticationException, IOException {
        return this.sendPostRequest(url, json, Proxy.NO_PROXY);
    }
    
    private String sendPostRequest(final String url, final String json, final Proxy proxy) throws AuthenticationException, IOException {
        final byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        final URL serverURL = new URL(url);
        final HttpURLConnection connection = (HttpURLConnection)serverURL.openConnection((proxy != null) ? proxy : Proxy.NO_PROXY);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        connection.setRequestProperty("Content-Length", String.valueOf(jsonBytes.length));
        final DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.write(jsonBytes, 0, jsonBytes.length);
        wr.flush();
        wr.close();
        connection.connect();
        final int responseCode = connection.getResponseCode();
        if (responseCode == 204) {
            connection.disconnect();
            return null;
        }
        InputStream is;
        if (responseCode == 200) {
            is = connection.getInputStream();
        }
        else {
            is = connection.getErrorStream();
        }
        final BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String response = br.readLine();
        try {
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        connection.disconnect();
        while (response != null && response.startsWith("\ufeff")) {
            response = response.substring(1);
        }
        if (responseCode == 200) {
            return response;
        }
        final Gson gson = new Gson();
        if (response != null && !response.startsWith("{")) {
            throw new AuthenticationException(new AuthError("Internal server error", response, "Remote"));
        }
        throw new AuthenticationException((AuthError)gson.fromJson(response, (Class)AuthError.class));
    }
}
