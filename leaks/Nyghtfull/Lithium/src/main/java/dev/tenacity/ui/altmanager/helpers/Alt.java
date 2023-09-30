package dev.tenacity.ui.altmanager.helpers;

import com.altening.auth.SSLController;
import com.altening.auth.TheAlteningAuthentication;
import com.altening.auth.service.AlteningServiceType;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import dev.tenacity.Tenacity;
import dev.tenacity.microsoft.MicrosoftLogin;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.server.ban.HypixelBan;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;

import java.io.IOException;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Alt {

    public static Minecraft mc = Minecraft.getMinecraft();
    public static int stage = -1;
    public static AltType currentLoginMethod = AltType.MOJANG;
    private final SSLController ssl = new SSLController();
    private final TheAlteningAuthentication alteningAuthentication = TheAlteningAuthentication.mojang();
    @Expose
    @SerializedName("uuid")
    public String uuid;
    @Expose
    @SerializedName("username")
    public String username;
    @Expose
    @SerializedName("email")
    public String email;
    @Expose
    @SerializedName("password")
    public String password;
    @Expose
    @SerializedName("altState")
    public AltState altState;
    @Expose
    @SerializedName("altType")
    public AltType altType;
    @Expose
    @SerializedName("hypixelBan")
    public HypixelBan hypixelBan;
    @Expose
    @SerializedName("favorite")
    public boolean favorite;

    public ResourceLocation head;
    public boolean headTexture;
    public int headTries;

    public Alt(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Alt() {
    }

    private void login(boolean microsoft) {
        stage = 0;
        if ((!microsoft && this.password.isEmpty())) {
            String uuid = UUID.randomUUID().toString();
            mc.session = new Session(this.email, uuid, "", "mojang");
            this.username = this.email;
            this.uuid = uuid;
            this.altState = AltState.LOGIN_SUCCESS;
            altType = AltType.CRACKED;
            stage = 2;
            Tenacity.INSTANCE.getAltManager().currentSessionAlt = this;
            return;
        }
        Session auth = this.createSession(this.email, this.password, microsoft);
        if (auth == null) {
            stage = 1;
            altState = AltState.LOGIN_FAIL;
        } else {
            mc.session = auth;
            uuid = auth.getPlayerID();
            username = auth.getUsername();
            stage = 2;
            altState = AltState.LOGIN_SUCCESS;
            altType = currentLoginMethod;
            Tenacity.INSTANCE.getAltManager().currentSessionAlt = this;
        }
    }

    public void loginAsync() {
        loginAsync(altType == AltType.MICROSOFT);
    }

    public void loginAsync(boolean microsoft) {
        new Thread(() -> {
            login(microsoft);
            new Thread(() -> {
                try {
                    Files.write(AltManagerUtils.altsFile.toPath(), new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create().toJson(AltManagerUtils.getAlts().toArray(new Alt[0])).getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }).start();
    }

    private Session createSession(String username, String password, boolean microsoft) {
        if (microsoft) {
            NotificationManager.post(NotificationType.INFO, "Alt Manager", "Opening browser to complete Microsoft authentication...", 12);
            CompletableFuture<Session> future = new CompletableFuture<>();
            MicrosoftLogin.getRefreshToken(refreshToken -> {
                if (refreshToken != null) {
                    System.out.println("Refresh token: " + refreshToken);
                    MicrosoftLogin.LoginData login = MicrosoftLogin.login(refreshToken);
                    currentLoginMethod = AltType.MICROSOFT;
                    future.complete(new Session(login.username, login.uuid, login.mcToken, "microsoft"));
                }
            });
            return future.join();
        } else {
            YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
            YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
            auth.setUsername(username);
            auth.setPassword(password);
            try {
                if (username.endsWith("@alt.com")) {
                    ssl.disableCertificateValidation();
                    alteningAuthentication.updateService(AlteningServiceType.THEALTENING);
                    auth.setPassword("sample pass");
                } else if (alteningAuthentication.getService() == AlteningServiceType.THEALTENING) {
                    ssl.enableCertificateValidation();
                    alteningAuthentication.updateService(AlteningServiceType.MOJANG);
                }
                auth.logIn();
                currentLoginMethod = AltType.MOJANG;
                return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(),
                        auth.getAuthenticatedToken(), "mojang");
            } catch (AuthenticationException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public String getType() {
        return altType == null ? "Not logged in" : altType.getName();
    }

    @Override
    public String toString() {
        return "Alt{" +
                "uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", altState=" + altState +
                ", altType=" + altType +
                ", headTries=" + headTries +
                '}';
    }

    @Getter
    @RequiredArgsConstructor
    public enum AltState {
        @Expose
        @SerializedName("1")
        LOGIN_FAIL(FontUtil.XMARK),

        @Expose
        @SerializedName("2")
        LOGIN_SUCCESS(FontUtil.CHECKMARK);
        private final String icon;
    }

    @Getter
    @RequiredArgsConstructor
    public enum AltType {
        @Expose
        @SerializedName("1")
        MICROSOFT("Microsoft"),

        @Expose
        @SerializedName("2")
        MOJANG("Mojang"),

        @Expose
        @SerializedName("3")
        CRACKED("Cracked");
        private final String name;
    }
}
