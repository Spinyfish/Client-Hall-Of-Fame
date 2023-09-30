package dev.africa.pandaware.impl.microshit.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.africa.pandaware.Client;
import dev.africa.pandaware.impl.ui.menu.account.Account;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class OAuthHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if ("GET".equals(httpExchange.getRequestMethod())) {
            Map<String, String> requestParameters = queryToMap(httpExchange.getRequestURI().getQuery());
            if (!requestParameters.containsKey("code") || !requestParameters.containsKey("state")) {
                String httpResponse = "400 Bad request";
                httpExchange.sendResponseHeaders(400, httpResponse.length());
                httpExchange.getResponseBody().write(httpResponse.getBytes(StandardCharsets.US_ASCII));
                return;
            }

            String code = requestParameters.get("code");
            String state = requestParameters.get("state");

            boolean reauth = requestParameters.containsKey("reauth") && Objects.equals(requestParameters.get("reauth"), "true");

            UUID uid = null;
            if (state.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")) {
                uid = UUID.fromString(state);
            }

            String client = httpExchange.getRemoteAddress().getHostString();
            if (httpExchange.getRequestHeaders().containsKey("X-Forwarded-For"))
                client = httpExchange.getRequestHeaders().getFirst("X-Forwarded-For").split(",")[0];

            try {
                MSTokenRequestor.TokenPair authToken;
                if (reauth) {
                    // System.out.println("> Refreshing TOKEN for " + client);
                    authToken = MSTokenRequestor.refreshFor(code);
                } else {
                    //  System.out.println("> Requesting TOKEN for " + client);
                    authToken = MSTokenRequestor.getFor(code);
                }

                //System.out.println("> Authenticating with XBL for " + client);
                XBLTokenRequestor.XBLToken xblToken = XBLTokenRequestor.getFor(authToken.token);

                // System.out.println("> Authenticating with XSTS for " + client);
                XSTSTokenRequestor.XSTSToken xstsToken = XSTSTokenRequestor.getFor(xblToken.token);

                // System.out.println("> Authenticating with Minecraft for " + client);
                MinecraftTokenRequestor.MinecraftToken minecraftToken = MinecraftTokenRequestor.getFor(xstsToken);

                // System.out.println("> Checking ownership and getting profile for " + client);
                MinecraftTokenRequestor.checkAccount(minecraftToken);
                MinecraftTokenRequestor.MinecraftProfile minecraftProfile = MinecraftTokenRequestor.getProfile(minecraftToken);

                JSONObject authResult = new JSONObject();
                authResult.put("access_token", minecraftToken.accessToken);
                authResult.put("refresh_token", authToken.refreshToken);
                authResult.put("uuid", minecraftProfile.uuid);
                authResult.put("name", minecraftProfile.name);
                authResult.put("skin", minecraftProfile.skinURL);
                String httpResponse = MicrosoftEncryption.encode(authResult.toString(), MicrosoftEncryption.getEncryptionPassword());

                if (uid != null) {
                    AuthManagerWebServer.authCache.put(uid, new AuthManagerWebServer.AuthInfo(System.currentTimeMillis(), httpResponse, client));
                }

                // add to alt-manager
                if (Client.getInstance().getMicrosoftProvider().expectingAltAdd) {
                    Client.getInstance().getMicrosoftProvider().expectingAltAdd = false;

                    String message = "Your account has now been added to the alt-manager, you can close this page.";

                    httpExchange.getResponseHeaders().add("Content-type", "application/json");
                    httpExchange.sendResponseHeaders(200, message.length());
                    httpExchange.getResponseBody().write(message.getBytes(StandardCharsets.US_ASCII));

                    Client.getInstance().getAccountManager().getItems().add(new Account(
                            "", authResult.getString("name"), "",
                            authResult.getString("refresh_token"), authResult.getString("uuid"),
                            false, false, true
                    ));
                    Client.getInstance().getFileManager().saveAll();
                } else {
                    httpExchange.getResponseHeaders().add("Content-type", "application/json");
                    httpExchange.sendResponseHeaders(200, httpResponse.length());
                    httpExchange.getResponseBody().write(httpResponse.getBytes(StandardCharsets.US_ASCII));
                }
            } catch (AuthenticationException e) {
                //   System.out.println("Auth error for "+client+"! "+e.getMessage());
                String httpResponse = "Authentication error: " + e.getMessage();
                httpExchange.sendResponseHeaders(401, httpResponse.length());
                httpExchange.getResponseBody().write(httpResponse.getBytes(StandardCharsets.US_ASCII));
            }
        } else {
            String httpResponse = "400 Bad request";
            httpExchange.sendResponseHeaders(400, httpResponse.length());
            httpExchange.getResponseBody().write(httpResponse.getBytes(StandardCharsets.US_ASCII));
        }
    }

    public Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }
}
