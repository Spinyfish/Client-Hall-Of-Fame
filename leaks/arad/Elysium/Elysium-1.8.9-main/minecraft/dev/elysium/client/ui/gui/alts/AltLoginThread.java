package dev.elysium.client.ui.gui.alts;

import java.net.Proxy;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

public final class AltLoginThread
        extends Thread {
    private final String password;
    private String status;
    private final String username;
    private Minecraft mc = Minecraft.getMinecraft();
    private final String combo;

    public AltLoginThread(String username, String password, String combo) {
        super("Alt Login Thread");
        this.username = username;
        this.password = password;
        this.combo = combo;
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

    public String getStatus() {
        return this.status;
    }

    @Override
    public void run() {
        System.out.println("running with " + username + " : " + password);
        if(this.password == "2053489reufiw" && this.username == "Empty"){
            this.status = "Clip board invalid / empty";
            return;
        } else {
            if(this.combo.isEmpty()){

                if (this.password.equals("") || this.password.equals("no_password") || this.password.equals("UNKNOWN")) {
                    this.mc.session = new Session(this.username, "", "", "mojang");
                    this.status ="Current offline acount: " + this.username;
                    return;
                }

                this.status = "Logging in...";
                Session auth = this.createSession(this.username, this.password);

                if (auth == null) {

                    this.status = "Login failed!";

                } else {

                    this.status = "Current online account: " + auth.getUsername();
                    this.mc.session = auth;
                }

            } else {

                if(combo.contains(":")) {
                    String combo = this.combo;
		    		/*
		    		int email_end = combo.indexOf(":")+1;
		    		String password = combo.substring(email_end);
		    		String email = combo.substring(0, email_end-1); //hunter ?
		    		*/

                    String email = combo.split(":")[0];
                    String password = combo.split(":")[1];


                    this.status = "Logging in...";
                    Session auth = this.createSession(email, password);

                    if (auth == null) {

                        this.status = "Login failed!";

                    } else {

                        this.status = "Current online account: " + auth.getUsername();
                        this.mc.session = auth;
                    }
                } else {
                    this.mc.session = new Session(this.combo, "", "", "mojang");
                    this.status = "Current offline account: " + this.combo;
                }
            }
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

