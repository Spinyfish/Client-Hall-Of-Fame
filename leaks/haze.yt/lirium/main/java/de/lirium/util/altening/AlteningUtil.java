package de.lirium.util.altening;

import com.eatthepath.uuid.FastUUID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import de.lirium.Client;
import de.lirium.util.altening.data.AccountInfo;
import de.lirium.util.altening.data.AltInfo;
import de.lirium.util.altening.data.AlteningSession;
import de.lirium.util.altening.enumeration.LicenseType;
import net.minecraft.util.Session;
import net.minecraft.util.Tuple;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.*;

//https://panel.thealtening.com/#api
public class AlteningUtil {

    private static final String API_URL = "https://api.thealtening.com/v2/";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static AlteningSession login(String token) {
        final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)
                new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(token);
        auth.setPassword(Client.NAME);
        try {
            auth.logIn();
            if (auth.getSelectedProfile() != null)
                return new AlteningSession(new Session(auth.getSelectedProfile().getName(), FastUUID.toString(auth.getSelectedProfile().getId()), auth.getAuthenticatedToken(), "mojang"), auth.getSelectedProfile().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AccountInfo getLicense(String apiKey) {
        try {
            final URL url = new URL(API_URL + "license?key=" + apiKey);
            final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            connection.setRequestProperty("Content-Type", "application/json");
            final InputStream stream = connection.getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            final JsonObject json = GSON.fromJson(reader, JsonObject.class);

            final String name = json.get("username").getAsString();
            final boolean hasLicense = json.get("hasLicense").getAsBoolean();
            if (hasLicense) {
                final LicenseType licenseType = Arrays.stream(LicenseType.values()).filter(license -> license.name.equalsIgnoreCase(json.get("licenseType").getAsString())).findFirst().orElse(LicenseType.NONE);
                final String expiresAt = json.get("expires").getAsString();
                return new AccountInfo(name, licenseType, expiresAt);
            } else {
                return new AccountInfo(name);
            }
        } catch (IOException | ClassCastException e) {
            e.printStackTrace();
            return new AccountInfo("unknown");
        }
    }

    public static AltInfo getInfo(String apiKey, String token) throws IOException {
        final URL url = new URL(API_URL + "info?key=" + apiKey + "&token=" + token);
        final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        final InputStream stream = connection.getInputStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final JsonObject json = GSON.fromJson(reader, JsonObject.class);
        final String username = json.get("username").getAsString();
        final String skin = json.has("skin") ? json.get("skin").getAsString() : null;
        return new AltInfo(token, username, false, skin);
    }

    public static boolean addFavorite(String apiKey, String token) throws IOException {
        final URL url = new URL(API_URL + "favorite?key=" + apiKey + "&token=" + token);
        final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        final InputStream stream = connection.getInputStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final JsonObject json = GSON.fromJson(reader, JsonObject.class);
        return json.get("success").getAsBoolean();
    }

    public static boolean addPrivate(String apiKey, String token) throws IOException {
        final URL url = new URL(API_URL + "private?key=" + apiKey + "&token=" + token);
        final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        final InputStream stream = connection.getInputStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final JsonObject json = GSON.fromJson(reader, JsonObject.class);
        return json.get("success").getAsBoolean();
    }

    public static List<AltInfo> getFavorites(String apiKey) throws IOException {
        final List<AltInfo> accounts = new ArrayList<>();
        final URL url = new URL(API_URL + "favorites?key=" + apiKey);
        final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        final InputStream stream = connection.getInputStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final JsonArray jsonArray = GSON.fromJson(reader, JsonArray.class);
        jsonArray.forEach(jsonElement -> {
            final JsonObject json = jsonElement.getAsJsonObject();
            final String token = json.get("token").getAsString();
            final String username = json.get("username").getAsString();
            final boolean limit = json.get("limit").getAsBoolean();
            final String skin = json.has("skin") ? json.get("skin").getAsString() : null;
            if (json.has("info")) {
                final JsonObject info = json.get("info").getAsJsonObject();
                final List<Tuple<String, String>> infoList = new ArrayList<>();
                info.entrySet().forEach(entry -> {
                    infoList.add(new Tuple<>(entry.getKey(), entry.getValue().getAsString()));
                });
                accounts.add(new AltInfo(token, username, limit, skin, infoList.toArray(new Tuple[0])));
            } else {
                accounts.add(new AltInfo(token, username, limit, skin));
            }
        });
        return accounts;
    }

    public static List<AltInfo> getPrivates(String apiKey) throws IOException {
        final List<AltInfo> accounts = new ArrayList<>();
        final URL url = new URL(API_URL + "privates?key=" + apiKey);
        final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        final InputStream stream = connection.getInputStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final JsonArray jsonArray = GSON.fromJson(reader, JsonArray.class);
        jsonArray.forEach(jsonElement -> {
            final JsonObject json = jsonElement.getAsJsonObject();
            final String token = json.get("token").getAsString();
            final String username = json.get("username").getAsString();
            final boolean limit = json.get("limit").getAsBoolean();
            final String skin = json.has("skin") ? json.get("skin").getAsString() : null;
            if (json.has("info")) {
                final JsonObject info = json.get("info").getAsJsonObject();
                final List<Tuple<String, String>> infoList = new ArrayList<>();
                info.entrySet().forEach(entry -> {
                    infoList.add(new Tuple<>(entry.getKey(), entry.getValue().getAsString()));
                });
                accounts.add(new AltInfo(token, username, limit, skin, infoList.toArray(new Tuple[0])));
            } else {
                accounts.add(new AltInfo(token, username, limit, skin));
            }
        });
        return accounts;
    }

    public static AltInfo generate(String apiKey) throws IOException {
        final URL url = new URL(API_URL + "generate?key=" + apiKey + "&info=true");
        final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        final InputStream stream = connection.getInputStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final JsonObject json = GSON.fromJson(reader, JsonObject.class);

        final String token = json.get("token").getAsString();
        final String username = json.get("username").getAsString();
        final boolean limitReached = json.get("limit").getAsBoolean();
        final String skin = json.has("skin") && !json.get("skin").isJsonNull() ? json.get("skin").getAsString() : null;
        System.out.println(token);
        if (json.has("info")) {
            final JsonObject info = json.get("info").getAsJsonObject();
            final List<Tuple<String, String>> infoList = new ArrayList<>();
            info.entrySet().forEach(entry -> {
                infoList.add(new Tuple<>(entry.getKey(), entry.getValue().getAsString()));
            });
            return new AltInfo(token, username, limitReached, skin, infoList.toArray(new Tuple[0]));
        }
        return new AltInfo(token, username, limitReached, skin);
    }
}
