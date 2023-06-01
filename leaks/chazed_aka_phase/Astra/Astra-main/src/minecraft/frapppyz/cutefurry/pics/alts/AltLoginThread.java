package frapppyz.cutefurry.pics.alts;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

import java.net.Proxy;

public final class AltLoginThread
extends Thread {
    private final String password;
    private String status;
    private final String username;
    private Minecraft mc = Minecraft.getMinecraft();

    public AltLoginThread(String username, String password) {
        super("Alt Login Thread");
        this.username = username;
        this.password = password;
        this.status = (Object)((Object)EnumChatFormatting.GRAY) + "Waiting...";
    }

    private Session createSession(String username, String password) {



        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)service.createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(username);
        auth.setPassword(password);
        try {
            auth.logIn();
            return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
        }
        catch (AuthenticationException localAuthenticationException) {
            localAuthenticationException.printStackTrace();
            return null;
        }
    }
    
    private Session createMicrosoftSession(final String username, final String password) {


        try {
        	MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
            MicrosoftAuthResult result = null;

            result = authenticator.loginWithCredentials(username, password);

            System.out.printf("Logged in with '%s'%n", result.getProfile().getName());

                System.out.println(result.getProfile().getId());
                return new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "legacy");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String getStatus() {
        return this.status;
    }

    @Override
    public void run() {
            if (this.password.equals("")) {
                this.mc.session = new Session(this.username, "", "", "mojang");
                this.status = (Object)((Object)EnumChatFormatting.GREEN) + "Logged in. (" + this.username + ")";
                return;
            }
            this.status = (Object)((Object)EnumChatFormatting.YELLOW) + "Logging in...";
            Session auth;
            auth = this.createMicrosoftSession(this.username, this.password);
            if (auth == null) {
                this.status = (Object)((Object)EnumChatFormatting.RED) + "Login failed!";
            } else {
                this.status = (Object)((Object)EnumChatFormatting.GREEN) + "Logged in. (" + auth.getUsername() + ")";
                this.mc.session = auth;
            }


    }

    public void setStatus(String status) {
        this.status = status;
    }
}

