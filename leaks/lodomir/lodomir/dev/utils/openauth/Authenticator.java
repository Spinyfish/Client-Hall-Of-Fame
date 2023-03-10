/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 */
package lodomir.dev.utils.openauth;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import lodomir.dev.utils.openauth.AuthPoints;
import lodomir.dev.utils.openauth.AuthenticationException;
import lodomir.dev.utils.openauth.model.AuthAgent;
import lodomir.dev.utils.openauth.model.AuthError;
import lodomir.dev.utils.openauth.model.request.AuthRequest;
import lodomir.dev.utils.openauth.model.request.InvalidateRequest;
import lodomir.dev.utils.openauth.model.request.RefreshRequest;
import lodomir.dev.utils.openauth.model.request.SignoutRequest;
import lodomir.dev.utils.openauth.model.request.ValidateRequest;
import lodomir.dev.utils.openauth.model.response.AuthResponse;
import lodomir.dev.utils.openauth.model.response.RefreshResponse;

public class Authenticator {
    public static final String MOJANG_AUTH_URL = "https://authserver.mojang.com/";
    private String authURL;
    private AuthPoints authPoints;

    public Authenticator(String authURL, AuthPoints authPoints) {
        this.authURL = authURL;
        this.authPoints = authPoints;
    }

    public AuthResponse authenticate(AuthAgent agent, String username, String password, String clientToken) throws AuthenticationException {
        AuthRequest request = new AuthRequest(agent, username, password, clientToken);
        return (AuthResponse)this.sendRequest(request, AuthResponse.class, this.authPoints.getAuthenticatePoint());
    }

    public RefreshResponse refresh(String accessToken, String clientToken) throws AuthenticationException {
        RefreshRequest request = new RefreshRequest(accessToken, clientToken);
        return (RefreshResponse)this.sendRequest(request, RefreshResponse.class, this.authPoints.getRefreshPoint());
    }

    public void validate(String accessToken) throws AuthenticationException {
        ValidateRequest request = new ValidateRequest(accessToken);
        this.sendRequest(request, null, this.authPoints.getValidatePoint());
    }

    public void signout(String username, String password) throws AuthenticationException {
        SignoutRequest request = new SignoutRequest(username, password);
        this.sendRequest(request, null, this.authPoints.getSignoutPoint());
    }

    public void invalidate(String accessToken, String clientToken) throws AuthenticationException {
        InvalidateRequest request = new InvalidateRequest(accessToken, clientToken);
        this.sendRequest(request, null, this.authPoints.getInvalidatePoint());
    }

    private Object sendRequest(Object request, Class<?> model, String authPoint) throws AuthenticationException {
        String response;
        Gson gson = new Gson();
        try {
            response = this.sendPostRequest(this.authURL + authPoint, gson.toJson(request));
        }
        catch (IOException e) {
            throw new AuthenticationException(new AuthError("Can't send the request : " + e.getClass().getName(), e.getMessage(), "Unknown"));
        }
        if (model != null) {
            return gson.fromJson(response, model);
        }
        return null;
    }

    private String sendPostRequest(String url, String json) throws AuthenticationException, IOException {
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        URL serverURL = new URL(url);
        HttpURLConnection connection = (HttpURLConnection)serverURL.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        connection.setRequestProperty("Content-Length", String.valueOf(jsonBytes.length));
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.write(jsonBytes, 0, jsonBytes.length);
        wr.flush();
        wr.close();
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode == 204) {
            connection.disconnect();
            return null;
        }
        InputStream is = responseCode == 200 ? connection.getInputStream() : connection.getErrorStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
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
        if (responseCode != 200) {
            Gson gson = new Gson();
            if (response != null && !response.startsWith("{")) {
                throw new AuthenticationException(new AuthError("Internal server error", response, "Remote"));
            }
            throw new AuthenticationException((AuthError)gson.fromJson(response, AuthError.class));
        }
        return response;
    }
}

