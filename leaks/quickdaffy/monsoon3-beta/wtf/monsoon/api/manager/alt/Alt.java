/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.Agent
 *  com.mojang.authlib.exceptions.AuthenticationException
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
 */
package wtf.monsoon.api.manager.alt;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.thealtening.auth.service.AlteningServiceType;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import java.net.Proxy;
import java.util.UUID;
import net.minecraft.util.Session;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.manager.alt.MicrosoftOAuth2Login;

public class Alt {
    private String email;
    private String password;
    private String username = "";
    private final Authenticator authenticator;
    private Session sessionBuffer;

    public Alt(String email, String password, Authenticator authenticator) {
        this.email = email;
        this.password = password;
        this.authenticator = authenticator;
    }

    public Session getSession() throws MicrosoftAuthenticationException {
        if (this.sessionBuffer == null) {
            this.sessionBuffer = this.loadSession();
        }
        return this.sessionBuffer;
    }

    public Session loadSession() throws MicrosoftAuthenticationException {
        switch (this.authenticator) {
            case MICROSOFT: {
                Wrapper.getMonsoon().getAltManager().getAlteningAuthentication().updateService(AlteningServiceType.MOJANG);
                MicrosoftAuthenticator auth = new MicrosoftAuthenticator();
                MicrosoftAuthResult result = auth.loginWithCredentials(this.email, this.password);
                Session session = new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "legacy");
                this.setUsername(session.getUsername());
                return session;
            }
            case CRACKED: {
                this.setUsername(this.email);
                return new Session(this.email, UUID.randomUUID().toString(), "", "legacy");
            }
            case ALTENING: {
                YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
                YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)service.createUserAuthentication(Agent.MINECRAFT);
                auth.setUsername(this.email);
                auth.setPassword("A");
                try {
                    auth.logIn();
                    Session session = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
                    this.setUsername(session.getUsername());
                    return session;
                }
                catch (AuthenticationException localAuthenticationException) {
                    localAuthenticationException.printStackTrace();
                    return null;
                }
            }
            case OAUTH: {
                MicrosoftOAuth2Login microsoftOAuth2 = new MicrosoftOAuth2Login();
                try {
                    MicrosoftAuthResult result = microsoftOAuth2.getAccessToken();
                    if (result != null) {
                        Session session = new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "legacy");
                        this.setUsername(session.getUsername());
                        return session;
                    }
                    return null;
                }
                catch (Exception exception) {
                    return null;
                }
            }
        }
        return null;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Authenticator getAuthenticator() {
        return this.authenticator;
    }

    public static enum Authenticator {
        MICROSOFT,
        CRACKED,
        ALTENING,
        OAUTH;

    }
}

