package client.account;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

public final class AltLoginThread extends Thread {
    private final String password;
    private String status;
    private final String username;
    private final Minecraft mc = Minecraft.getMinecraft();

    public AltLoginThread(String username, String password) {
        super("Alt Login Thread");
        this.username = username;
        this.password = password;
        this.status = EnumChatFormatting.GRAY + "Waiting...";
    }

    public String getStatus() {
        return this.status;
    }

    @Override
    public void run() {
        if (this.password.equals("")) {
            this.mc.session = new Session(this.username, "", "", "msa");
            this.status = EnumChatFormatting.GREEN + "Logged in. (" + this.username + " - offline name)";
            return;
        }
        this.status = EnumChatFormatting.YELLOW + "Logging in...";
        MicrosoftAuthenticator microsoftAuthenticator = new MicrosoftAuthenticator();
        try {
            MicrosoftAuthResult microsoftAuthResult = microsoftAuthenticator.loginWithCredentials(this.username, this.password);
            AltManager.lastAlt = new Alt(this.username, this.password);
            this.status = EnumChatFormatting.GREEN + "Logged in. (" + microsoftAuthResult.getProfile().getName() + ")";
            this.mc.session = new Session(microsoftAuthResult.getProfile().getName(), microsoftAuthResult.getProfile().getId(), microsoftAuthResult.getAccessToken(), "msa");
        } catch (MicrosoftAuthenticationException e) {
            e.printStackTrace();
            this.status = EnumChatFormatting.RED + "Login failed!";
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

