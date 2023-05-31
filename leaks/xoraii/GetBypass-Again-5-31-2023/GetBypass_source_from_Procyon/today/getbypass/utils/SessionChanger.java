// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.utils;

import net.minecraft.util.Session;
import com.mojang.util.UUIDTypeAdapter;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.Minecraft;
import java.util.UUID;
import com.mojang.authlib.UserAuthentication;

public class SessionChanger
{
    private static SessionChanger instance;
    private final UserAuthentication auth;
    
    public static SessionChanger getInstance() {
        if (SessionChanger.instance == null) {
            SessionChanger.instance = new SessionChanger();
        }
        return SessionChanger.instance;
    }
    
    private SessionChanger() {
        final UUID notSureWhyINeedThis = UUID.randomUUID();
        final AuthenticationService authService = (AuthenticationService)new YggdrasilAuthenticationService(Minecraft.getMinecraft().getProxy(), notSureWhyINeedThis.toString());
        this.auth = authService.createUserAuthentication(Agent.MINECRAFT);
        authService.createMinecraftSessionService();
    }
    
    public void setUser(final String email, final String password) {
        if (!Minecraft.getMinecraft().getSession().getUsername().equals(email) || Minecraft.getMinecraft().getSession().getToken().equals("0")) {
            this.auth.logOut();
            this.auth.setUsername(email);
            this.auth.setPassword(password);
            try {
                this.auth.logIn();
                final Session session = new Session(this.auth.getSelectedProfile().getName(), UUIDTypeAdapter.fromUUID(this.auth.getSelectedProfile().getId()), this.auth.getAuthenticatedToken(), this.auth.getUserType().getName());
                this.setSession(session);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void setSession(final Session session) {
        Minecraft.getMinecraft().session = session;
    }
    
    public void setUserOffline(final String username) {
        this.auth.logOut();
        final Session session = new Session(username, username, "0", "legacy");
        this.setSession(session);
    }
}
