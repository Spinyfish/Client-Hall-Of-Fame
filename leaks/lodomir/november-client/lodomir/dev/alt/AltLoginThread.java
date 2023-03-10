/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.alt;

import lodomir.dev.November;
import lodomir.dev.alt.Alt;
import lodomir.dev.alt.AltManager;
import lodomir.dev.utils.openauth.microsoft.MicrosoftAuthResult;
import lodomir.dev.utils.openauth.microsoft.MicrosoftAuthenticationException;
import lodomir.dev.utils.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

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
        this.status = (Object)((Object)EnumChatFormatting.GRAY) + "Waiting...   ";
    }

    private Session createSession(String email, String password) {
        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        try {
            MicrosoftAuthResult acc = authenticator.loginWithCredentials(email, password);
            Minecraft.getMinecraft().session = new Session(acc.getProfile().getName(), acc.getProfile().getId(), acc.getAccessToken(), "legacy");
        }
        catch (MicrosoftAuthenticationException microsoftAuthenticationException) {
            // empty catch block
        }
        return null;
    }

    private void setSession(Session session) {
        Minecraft.getMinecraft().session = session;
    }

    public String getStatus() {
        return this.status;
    }

    @Override
    public void run() {
        if (this.password.equals("")) {
            this.mc.session = new Session(this.username, "", "", "microsoft");
            this.status = (Object)((Object)EnumChatFormatting.GREEN) + "Logged in. (" + this.username + " - offline name)";
            return;
        }
        this.status = (Object)((Object)EnumChatFormatting.YELLOW) + "Logging in...";
        MicrosoftAuthenticator auth = new MicrosoftAuthenticator();
        if (auth == null) {
            this.status = (Object)((Object)EnumChatFormatting.RED) + "Login failed!";
        } else {
            AltManager altManager = November.INSTANCE.altManager;
            AltManager.lastAlt = new Alt(this.username, this.password);
            this.status = (Object)((Object)EnumChatFormatting.GREEN) + "Logged in. (" + this.mc.session.getUsername() + ")";
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

