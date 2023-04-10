/*
 * Decompiled with CFR 0_122.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.Agent
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.UserAuthentication
 *  com.mojang.authlib.exceptions.AuthenticationException
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
 */
package arithmo.gui.altmanager;

import java.net.Proxy;
import java.util.UUID;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.thealtening.auth.SSLController;
import com.thealtening.auth.TheAlteningAuthentication;
import com.thealtening.auth.service.AlteningServiceType;


import crispy.Crispy;
import crispy.notification.NotificationPublisher;
import crispy.notification.NotificationType;
import crispy.util.server.CapeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

public final class AltLoginThread extends Thread
{
	String banned = "false";
    private final String password;
    private String status;
    private final String username;
    private Minecraft mc;
    private SSLController ssl;
    private TheAlteningAuthentication serviceSwitch;
    
    public AltLoginThread(final String username, final String password) {
        super("Alt Login Thread");
        this.mc = Minecraft.getMinecraft();
        this.ssl = new SSLController();
        this.serviceSwitch = TheAlteningAuthentication.mojang();
        this.username = username;
        this.password = password;
    }
    
    private Session createSession(final String username, final String password) {
        final YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)service.createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(username);
        auth.setPassword(password);
        try {
            if (username.endsWith("@alt.com")) {
                this.ssl.disableCertificateValidation();
                this.serviceSwitch.updateService(AlteningServiceType.THEALTENING);
            }
            else if (this.serviceSwitch.getService() == AlteningServiceType.THEALTENING) {
                this.ssl.enableCertificateValidation();
                this.serviceSwitch.updateService(AlteningServiceType.MOJANG);
            }
            auth.logIn();


            return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
        }
        catch (AuthenticationException localAuthenticationException) {
            localAuthenticationException.printStackTrace();
            return null;
        }
        catch (NullPointerException XD3) {
            try {
                auth.logIn();
            }
            catch (AuthenticationException e) {
                e.printStackTrace();
            }
            System.out.print(auth.getAuthenticatedToken());

            return new Session(username.split("@")[0], UUID.randomUUID().toString(), auth.getAuthenticatedToken(), "mojang");
        }
        
    }
    
    public String getStatus() {
        return this.status;
    }


    public void run() {

        if (this.password.equals("")) {
            this.mc.session = new Session(this.username.replace("&", "�"), "", "", "mojang");
            this.status = EnumChatFormatting.GREEN + "Logged in. (" + this.username + " - offline name)";
            NotificationPublisher.queue("Login", "Successfully logged in to " + mc.session.getUsername(), NotificationType.SUCCESS);

            CapeManager capeManager = new CapeManager();
            System.out.println(mc.session.getUsername());
            capeManager.sendCapeRequest(mc.session.getUsername(), "");

            return;
        }
        this.status = EnumChatFormatting.AQUA + "Logging in...";

        final Session auth = this.createSession(this.username, this.password);

        if (auth == null) {
            this.status = EnumChatFormatting.RED + "Login failed!";
            NotificationPublisher.queue("Login", "ERROR: Failed to log into", NotificationType.ERROR);


        }
        else {
            AltManager.lastAlt = new Alt(this.username, this.password);
            //
            NotificationPublisher.queue("Login", "Successfully logged in to " + auth.getUsername(), NotificationType.SUCCESS);
            CapeManager capeManager = new CapeManager();
            this.status = EnumChatFormatting.GREEN + "Logged in. (" + auth.getUsername();
            this.mc.session = auth;

            capeManager.sendCapeRequest(mc.session.getUsername(), "");

            this.status = EnumChatFormatting.GREEN + "Logged in. (" + auth.getUsername() + ")";




        }
    }
    
    public void setStatus(final String status) {
        this.status = status;
    }
}
