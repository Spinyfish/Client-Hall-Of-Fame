package frapppyz.cutefurry.pics.alts;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.guis.GuiLogin;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;

import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;

public class CookieLoginTYBabyPlus {

    private static final String CLIENT_ID = "c0d977c5-e092-4c31-b7cc-3b2d7b7a337a";
    private static final String CLIENT_SECRET = "qpS8Q~2zhfJ7KIRDha7EGKeQi8Z1.h4NL04Kxc1w";

    private final String clientId;
    private final String clientSecret;

    private String username;
    private String uuid;
    private String accessToken;

    private final String redirectUri = "http://localhost/api";
    private final String loginUri;

    private String authCode;
    private String xboxAccessToken;
    private String xblToken;
    private String xboxUserhash;
    private String xstsToken;

    private String refreshToken;
    private boolean shouldRefreshLogin;

    private final File file;

    public CookieLoginTYBabyPlus() {
        this.clientId = CLIENT_ID;
        this.clientSecret = CLIENT_SECRET;

        loginUri = "https://login.microsoftonline.com/consumers/oauth2/v2.0/authorize?client_id=" + clientId + "&response_type=code&redirect_uri=" + redirectUri + "&scope=XboxLive.signin%20offline_access";
        try {
            Desktop.getDesktop().browse(URI.create(loginUri));
        } catch(Exception ex) {

        }
        File ROOT_DIR = new File("Browser");
        if (!ROOT_DIR.exists()) {
            ROOT_DIR.mkdirs();
        } else {
            ROOT_DIR.delete();
            ROOT_DIR.mkdirs();
        }

        file = new File(ROOT_DIR, "token.json");
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        loadRefreshToken();
    }

    public void login() {
        if (shouldRefreshLogin) {
            try {
                HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
                server.createContext("/api", new MyHandler(server, this));
                server.setExecutor(null);
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            getToken(false);
        }
    }

    public void getToken(boolean freshLogin) {
        HttpPost post = new HttpPost("https://login.microsoftonline.com/consumers/oauth2/v2.0/token");
        ArrayList<NameValuePair> urlParameters = new ArrayList<>();
        if (freshLogin) {
            urlParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
            urlParameters.add(new BasicNameValuePair("code", authCode));
            urlParameters.add(new BasicNameValuePair("client_id", clientId));
            urlParameters.add(new BasicNameValuePair("redirect_uri", redirectUri));
            urlParameters.add(new BasicNameValuePair("client_secret", clientSecret));

            try {
                post.setEntity(new UrlEncodedFormEntity(urlParameters));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            post.addHeader("Content-type", "application/x-www-form-urlencoded");

            try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
                String json = EntityUtils.toString(response.getEntity());
                JsonObject jobj = new Gson().fromJson(json, JsonObject.class);
                xboxAccessToken = jobj.get("access_token").getAsString();

                refreshToken = jobj.get("refresh_token").getAsString();
                saveRefreshToken();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            urlParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
            urlParameters.add(new BasicNameValuePair("refresh_token", refreshToken));
            urlParameters.add(new BasicNameValuePair("client_id", clientId));
            urlParameters.add(new BasicNameValuePair("client_secret", clientSecret));
            urlParameters.add(new BasicNameValuePair("scope", "XboxLive.signin"));

            try {
                post.setEntity(new UrlEncodedFormEntity(urlParameters));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            post.addHeader("Content-type", "application/x-www-form-urlencoded");

            try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
                String json = EntityUtils.toString(response.getEntity());
                JsonObject jobj = new Gson().fromJson(json, JsonObject.class);
                xboxAccessToken = jobj.get("access_token").getAsString();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        authXBL();
    }

    public void authXBL() {
        HttpPost post = new HttpPost("https://user.auth.xboxlive.com/user/authenticate");

        post.setHeader("Content-type", "application/json");
        post.setHeader("Accept", "application/json");

        String payload = "{\"Properties\": {\"AuthMethod\": \"RPS\", \"SiteName\": \"user.auth.xboxlive.com\", \"RpsTicket\": \"d=" + xboxAccessToken + "\"},\"RelyingParty\": \"http://auth.xboxlive.com\", \"TokenType\": \"JWT\"}";
        StringEntity requestEntity = new StringEntity(payload, ContentType.APPLICATION_JSON);
        post.setEntity(requestEntity);

        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
            String json = EntityUtils.toString(response.getEntity());
            JsonObject jobj = new Gson().fromJson(json, JsonObject.class);
            xblToken = jobj.get("Token").getAsString();
            xboxUserhash = jobj.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        authXSTS();
    }

    private void authXSTS() {
        HttpPost post = new HttpPost("https://xsts.auth.xboxlive.com/xsts/authorize");

        post.setHeader("Content-type", "application/json");
        post.setHeader("Accept", "application/json");

        String payload = "{\"Properties\": {\"SandboxId\": \"RETAIL\", \"UserTokens\": [\"" + xblToken + "\"]}, \"RelyingParty\": \"rp://api.minecraftservices.com/\", \"TokenType\": \"JWT\"}";
        StringEntity requestEntity = new StringEntity(payload, ContentType.APPLICATION_JSON);
        post.setEntity(requestEntity);

        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
            String json = EntityUtils.toString(response.getEntity());
            JsonObject jobj = new Gson().fromJson(json, JsonObject.class);
            xstsToken = jobj.get("Token").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        authMinecraft();
    }

    private void authMinecraft() {
        HttpPost post = new HttpPost("https://api.minecraftservices.com/authentication/login_with_xbox");

        String payload = "{\"identityToken\": \"XBL3.0 x=" + xboxUserhash + ";" + xstsToken + "\"}";
        StringEntity requestEntity = new StringEntity(payload, ContentType.APPLICATION_JSON);
        post.setEntity(requestEntity);

        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
            String json = EntityUtils.toString(response.getEntity());
            JsonObject jobj = new Gson().fromJson(json, JsonObject.class);
            accessToken = jobj.get("access_token").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        checkOwnership();
    }

    private void checkOwnership() {
        getProfile();
    }

    private void getProfile() {
        HttpGet get = new HttpGet("https://api.minecraftservices.com/minecraft/profile");

        get.setHeader("Authorization", "Bearer " + accessToken);

        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(get)) {
            String json = EntityUtils.toString(response.getEntity());
            JsonObject jobj = new Gson().fromJson(json, JsonObject.class);
            uuid = jobj.get("id").getAsString();
            username = jobj.get("name").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setSession();
    }

    private void setSession() {
        Minecraft.getMinecraft().session = (new Session(username, uuid, accessToken, "mojang"));
        Wrapper.getLogger().info("Logged in as " + username + ".");
    }

    static class MyHandler implements HttpHandler {
        private final HttpServer server;
        private final CookieLoginTYBabyPlus changer;

        public MyHandler(HttpServer server, CookieLoginTYBabyPlus changer) {
            this.server = server;
            this.changer = changer;
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            String responseURI = t.getRequestURI().toString();
            String code = responseURI.split("=")[1];

            String response = "Logged in! You can now close this window!";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            server.stop(1);

            changer.authCode = code;
            changer.getToken(true);
        }

    }

    public void saveRefreshToken() {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(refreshToken.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadRefreshToken() {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }

            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();

            String token = builder.toString();

            if (token.equals("")) {
                shouldRefreshLogin = true;
            } else {
                shouldRefreshLogin = false;
                refreshToken = token;
            }
        } catch (IOException e) {
            e.printStackTrace();
            shouldRefreshLogin = true;
        }
    }
}