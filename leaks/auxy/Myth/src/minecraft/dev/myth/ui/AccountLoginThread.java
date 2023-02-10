/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 23:58
 */
package dev.myth.ui;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class AccountLoginThread extends Thread {

    private final String username;
    private final String password;

    public String status = "Logging in...";

    public AccountLoginThread(String username, String password) {
        super("AccountLoginThread");
        this.username = username;
        this.password = password;
    }

    @Override
    public void run() {
        Session session = null;

        if(username.isEmpty()) {
            status = "Username cannot be empty!";
            return;
        }

        if(password.isEmpty()) {
            session = new Session(username, "", "", "legacy");
            status = "Logging in as " + username + " (cracked)...";
        } else {
            status = "Logging in as " + username + "...";

            MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
            try {
                MicrosoftAuthResult auth = authenticator.loginWithCredentials(username, password);
                session = new Session(auth.getProfile().getName(), auth.getProfile().getId(), auth.getAccessToken(), "mojang");
            } catch (Exception exception) {
                exception.printStackTrace();
                status = "Failed to authenticate as " + username + "!";
                return;
            }
        }

        Minecraft.getMinecraft().session = session;

        status = "Logged in as " + session.getUsername() + "! " + (password.isEmpty() ? "(cracked)" : "");
    }

}
