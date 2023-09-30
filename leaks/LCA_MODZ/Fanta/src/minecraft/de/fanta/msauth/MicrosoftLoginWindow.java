package de.fanta.msauth;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import sun.net.www.protocol.https.Handler;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class MicrosoftLoginWindow extends JFrame {

    private static final String CLIENT_ID = "00000000402b5328";
    private static final String MC_STORE_ITEM_1 = "product_minecraft";
    private static final String MC_STORE_ITEM_2 = "game_minecraft";
    public static EAuthenticationSteps authenticationStep = EAuthenticationSteps.IDLE;
    private static MicrosoftLoginWindow instance;

    public MicrosoftLoginWindow() {
        instance = this;
        this.setTitle("Connect with Microsoft");
        this.setSize(600, 700);
        this.setLocationRelativeTo(null);
        this.setResizable(true);
        this.setContentPane(new JFXPanel());
        Platform.runLater(this::loadScene);
        this.overrideWindow();
    }

    public static MicrosoftLoginWindow getInstance() {
        return instance;
    }

    public void start() {
        Platform.runLater(this::loadScene);
    }

    private void loadScene() {
        authenticationStep = EAuthenticationSteps.AUTHORIZE;

        WebView webView = new WebView();
        JFXPanel content = (JFXPanel) this.getContentPane();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                reset();
                System.out.println("User closed window");
            }
        });

        webView.getEngine().load("https://login.live.com/oauth20_authorize.srf?client_id=" + CLIENT_ID + "&response_type=code&redirect_uri=https://login.live.com/oauth20_desktop.srf&scope=XboxLive.signin%20offline_access&prompt=login");
        content.setScene(new Scene(webView, this.getWidth(), this.getHeight()));
        this.setVisible(true);
    }

    public void overrideWindow() {
        try {
            URL.setURLStreamHandlerFactory(protocol -> {

                if (!protocol.equals("https")) return null;
                return new Handler() {
                    @Override
                    protected URLConnection openConnection(URL url, Proxy proxy) throws IOException {
                        HttpURLConnection connection = (HttpURLConnection) super.openConnection(url, proxy);

                        if (url.toString().contains("denied")) {
                            System.out.println("Denied Connection");
                            reset();
                        } else if (url.toString().contains("https://login.live.com/oauth20_desktop.srf?code") && authenticationStep == EAuthenticationSteps.AUTHORIZE) {
                            getMicrosoftToken(url);
                            setVisible(false);
                        }
                        return connection;
                    }
                };
            });
        } catch (Error ignored) {
            System.out.println("Override already applied");
        }
    }

    private void getMicrosoftToken(URL tokenURL) {
        System.out.println("Token: " + tokenURL.toString().split("=")[1]);
        authenticationStep = EAuthenticationSteps.MS_TOKEN;

        JsonObject response = URLUtils.readJSONFromURL("https://login.live.com/oauth20_token.srf?client_id=" + CLIENT_ID + "&grant_type=authorization_code&redirect_uri=https://login.live.com/oauth20_desktop.srf&code=" + tokenURL.toString().split("=")[1], null);
        System.out.println("Access Token: " + response.get("access_token").getAsString());
        getXboxLiveToken(response.get("access_token").getAsString());
    }

    private void getXboxLiveToken(String token) {
        authenticationStep = EAuthenticationSteps.XBOXLIVE;
        JsonObject properties = new JsonObject();
        properties.addProperty("AuthMethod", "RPS");
        properties.addProperty("SiteName", "user.auth.xboxlive.com");
        properties.addProperty("RpsTicket", "d=" + token);

        JsonObject request = new JsonObject();
        request.add("Properties", properties);
        request.addProperty("RelyingParty", "http://auth.xboxlive.com");
        request.addProperty("TokenType", "JWT");

        JsonObject response = URLUtils.postJson("https://user.auth.xboxlive.com/user/authenticate", request);
        System.out.println("Xbox Live Access Token: " + response.get("Token"));
        getXSTS(response.get("Token").getAsString());
    }

    private void getXSTS(String token) {
        authenticationStep = EAuthenticationSteps.XSTS;
        JsonPrimitive jsonToken = new JsonPrimitive(token);
        JsonArray userTokens = new JsonArray();
        userTokens.add(jsonToken);

        JsonObject properties = new JsonObject();
        properties.addProperty("SandboxId", "RETAIL");
        properties.add("UserTokens", userTokens);

        JsonObject request = new JsonObject();
        request.add("Properties", properties);
        request.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
        request.addProperty("TokenType", "JWT");

        JsonObject response = URLUtils.postJson("https://xsts.auth.xboxlive.com/xsts/authorize", request);
        if (response.has("XErr")) {
            switch (response.get("XErr").getAsString()) {
                case "2148916233":
                    System.out.println("This account doesn't have an Xbox account.");
                    break;

                case "2148916235":
                    System.out.println("Xbox isn't available in your country.");
                    break;

                case "2148916238":
                    System.out.println("The account is under 18 and must be added to a Family (https://start.ui.xboxlive.com/AddChildToFamily)");
                    break;
            }
            this.reset();
        } else {
            System.out.println("XSTS Token: " + response.get("Token"));
            getMinecraftToken(response.getAsJsonObject("DisplayClaims").get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString(), response.get("Token").getAsString());
        }
    }

    private void getMinecraftToken(String uhs, String token) {
        authenticationStep = EAuthenticationSteps.MCAUTH;

        JsonObject request = new JsonObject();
        request.addProperty("identityToken", String.format("XBL3.0 x=%s;%s", uhs, token));

        JsonObject response = URLUtils.postJson("https://api.minecraftservices.com/authentication/login_with_xbox", request);
        System.out.println("Minecraft Token: " + response.get("access_token"));

        checkMinecraftOwnership(response.get("access_token").getAsString());
    }

    private void checkMinecraftOwnership(String token) {
        authenticationStep = EAuthenticationSteps.CHECK_OWNERSHIP;
        Map<String, String> headers = new HashMap<>();
        boolean ownsMinecraft = true;

        headers.put("Authorization", "Bearer " + token);
        JsonObject request = URLUtils.readJSONFromURL("https://api.minecraftservices.com/entitlements/mcstore", headers);

        for (int i = 0; i < request.get("items").getAsJsonArray().size(); i++) {
            String itemName = request.get("items").getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString();
            if (itemName.equals(MC_STORE_ITEM_1) || itemName.equals(MC_STORE_ITEM_2)) {
                ownsMinecraft = true;
                break;
            }
        }

        if (!ownsMinecraft) System.out.println("User doesn't own Minecraft");
        else getMinecraftProfile(token);
    }

    private void getMinecraftProfile(String token) {
        authenticationStep = EAuthenticationSteps.MC_PROFILE;
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        JsonObject request = URLUtils.readJSONFromURL("https://api.minecraftservices.com/minecraft/profile", headers);
        System.out.println("Account Owner: " + request.get("name"));
        Minecraft.getMinecraft().session = new Session(request.get("name").getAsString(), request.get("id").getAsString(), token, "mojang");
        authenticationStep = EAuthenticationSteps.DONE;
        reset();
    }

    private void reset() {
        this.setVisible(false);
        authenticationStep = EAuthenticationSteps.IDLE;
    }

    private enum EAuthenticationSteps {
        IDLE, AUTHORIZE, MS_TOKEN, XBOXLIVE, XSTS, MCAUTH, CHECK_OWNERSHIP, MC_PROFILE, DONE
    }

}
