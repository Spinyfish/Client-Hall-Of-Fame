/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package wtf.monsoon.api.manager.alt;

import com.sun.net.httpserver.HttpServer;
import fr.litarvan.openauth.microsoft.HttpClient;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.model.response.MinecraftProfile;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.manager.alt.Alt;
import wtf.monsoon.impl.ui.menu.windows.AltWindow;
import wtf.monsoon.misc.server.packet.impl.MPacketUpdateUsername;

public class MicrosoftOAuth2Login {
    private MicrosoftAuthResult response;

    public MicrosoftAuthResult getAccessToken() throws Exception {
        HttpClient http = new HttpClient();
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        String authUrl = "https://login.live.com/oauth20_authorize.srf?client_id=2fa2913a-ebf9-4bb7-b16a-c3e7e437b5bc&response_type=code&redirect_uri=http://localhost:8080/&scope=XboxLive.signin%20offline_access";
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(authUrl);
        clip.setContents(stringSelection, stringSelection);
        AltWindow.setStatus("OAuth URL copied to clipboard.");
        httpServer.createContext("/", exchange -> {
            String code = exchange.getRequestURI().toString();
            code = code.substring(code.lastIndexOf(61) + 1);
            String token = "https://login.live.com/oauth20_token.srf";
            try {
                Session session;
                JSONObject tokenMap = new JSONObject();
                tokenMap.put("client_id", (Object)"2fa2913a-ebf9-4bb7-b16a-c3e7e437b5bc");
                tokenMap.put("code", (Object)code);
                tokenMap.put("grant_type", (Object)"authorization_code");
                tokenMap.put("redirect_uri", (Object)"http://localhost:8080/");
                tokenMap.put("client_secret", (Object)"yax8Q~qfPbqyR_6Qmau3J93dVFuO5cvk4w4uQcLk");
                String oauth = http.readPost("https://login.live.com/oauth20_token.srf", this.toMap(tokenMap), "application/x-www-form-urlencoded");
                String access_token = new JSONObject(oauth).getString("access_token");
                JSONObject authObj = new JSONObject();
                authObj.put("RelyingParty", (Object)"http://auth.xboxlive.com");
                authObj.put("TokenType", (Object)"JWT");
                JSONObject propObj = new JSONObject();
                propObj.put("RpsTicket", (Object)("d=" + access_token));
                propObj.put("SiteName", (Object)"user.auth.xboxlive.com");
                propObj.put("AuthMethod", (Object)"RPS");
                authObj.put("Properties", (Object)propObj);
                JSONObject xbl = new JSONObject(http.readPost("https://user.auth.xboxlive.com/user/authenticate", authObj, "application/json", "application/json"));
                String xbl_token = xbl.getString("Token");
                String xbl_uhs = xbl.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0).getString("uhs");
                JSONObject authObj2 = new JSONObject();
                authObj2.put("RelyingParty", (Object)"rp://api.minecraftservices.com/");
                authObj2.put("TokenType", (Object)"JWT");
                JSONObject propObj2 = new JSONObject();
                propObj2.put("SandboxId", (Object)"RETAIL");
                propObj2.put("UserTokens", (Object)new String[]{xbl_token});
                authObj2.put("Properties", (Object)propObj2);
                JSONObject xsts = new JSONObject(http.readPost("https://xsts.auth.xboxlive.com/xsts/authorize", authObj2, "application/json", "application/json"));
                String xsts_token = xsts.getString("Token");
                String xsts_uhs = xsts.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0).getString("uhs");
                JSONObject xboxAuth = new JSONObject();
                xboxAuth.put("identityToken", (Object)String.format("XBL3.0 x=%s;%s", xsts_uhs, xsts_token));
                xboxAuth.put("ensureLegacyEnabled", (Object)"true");
                JSONObject resp = new JSONObject(http.readPost("https://api.minecraftservices.com/authentication/login_with_xbox", xboxAuth, "application/json"));
                MinecraftProfile profile = http.getJson("https://api.minecraftservices.com/minecraft/profile", resp.getString("access_token"), MinecraftProfile.class);
                this.response = new MicrosoftAuthResult(profile, resp.getString("access_token"));
                Minecraft.getMinecraft().session = session = new Session(this.response.getProfile().getName(), this.response.getProfile().getId(), this.response.getAccessToken(), "legacy");
                Alt alt = new Alt(this.response.getProfile().getName() + " - OAuth", "nopassword", Alt.Authenticator.OAUTH);
                AltWindow.setStatus("Logged in as " + Minecraft.getMinecraft().session.getUsername());
                Wrapper.getMonsoon().getServer().sendPacket(new MPacketUpdateUsername(Minecraft.getMinecraft().session.getUsername()));
                Wrapper.getMonsoon().getAltManager().addAlt(alt);
                Wrapper.getMonsoon().getConfigSystem().saveAlts(Wrapper.getMonsoon().getAltManager());
                String success = "Successfully logged into account " + this.response.getProfile().getName();
                exchange.sendResponseHeaders(200, success.length());
                OutputStream responseBody = exchange.getResponseBody();
                responseBody.write(success.getBytes(StandardCharsets.UTF_8));
                responseBody.close();
                httpServer.stop(2);
            }
            catch (Exception ignored) {
                httpServer.stop(2);
            }
        });
        httpServer.setExecutor(null);
        httpServer.start();
        return this.response;
    }

    private Map<String, Object> toMap(JSONObject jsonobj) throws JSONException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Iterator keys = jsonobj.keys();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            List<Object> value = jsonobj.get(key);
            if (value instanceof JSONArray) {
                value = this.toList((JSONArray)value);
            } else if (value instanceof JSONObject) {
                value = this.toMap((JSONObject)value);
            }
            map.put(key, value);
        }
        return map;
    }

    private List<Object> toList(JSONArray array) throws JSONException {
        ArrayList<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); ++i) {
            List<Object> value = array.get(i);
            if (value instanceof JSONArray) {
                value = this.toList((JSONArray)value);
            } else if (value instanceof JSONObject) {
                value = this.toMap((JSONObject)value);
            }
            list.add(value);
        }
        return list;
    }
}

