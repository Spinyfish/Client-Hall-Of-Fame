package felix.gui.alt.gui.thread;

import com.mojang.authlib.Agent;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.realmsclient.gui.ChatFormatting;

import felix.Client;
import felix.gui.alt.gui.GuiAltManager;
import felix.gui.alt.gui.impl.GuiAlteningLogin;
import felix.gui.alt.system.Account;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.util.Session;

import java.net.Proxy;
import java.util.UUID;

public class AccountLoginThread extends Thread {

    private String email, password;

    private String status = "Waiting for login...";

    public AccountLoginThread(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public void run() {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiAlteningLogin || (Minecraft.getMinecraft().currentScreen instanceof GuiDisconnected)) Client.INSTANCE.switchToTheAltening();
        else Client.INSTANCE.switchToMojang();
        status = "Logging in...";

        YggdrasilAuthenticationService yService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
        UserAuthentication userAuth = yService.createUserAuthentication(Agent.MINECRAFT);

        if (userAuth == null) {
            status = ChatFormatting.RED + "Unknown error.";
            return;
        }

        userAuth.setUsername(email);
        userAuth.setPassword(password);
        try {
            userAuth.logIn();


            Session session = new Session(userAuth.getSelectedProfile().getName(), userAuth.getSelectedProfile().getId().toString(), userAuth.getAuthenticatedToken(), email.contains("@") ? "mojang" : "legacy");

            Minecraft.getMinecraft().setSession(session);

            Account account = new Account(email,password,email);
            account.setName(session.getUsername());
            if (!(Minecraft.getMinecraft().currentScreen instanceof GuiAlteningLogin || Minecraft.getMinecraft().currentScreen instanceof GuiDisconnected))
                Client.INSTANCE.getAccountManager().setLastAlt(account);
            Client.INSTANCE.getAccountManager().save();
            GuiAltManager.currentAccount = account;
            status = String.format(ChatFormatting.GREEN + "Logged in as %s.", account.getName());
        } catch (AuthenticationException exception) {
            status = ChatFormatting.RED + "Login failed.";
        } catch (NullPointerException exception) {
            status = ChatFormatting.RED + "Unknown error.";
        }
    }

    public String getStatus() {
        return status;
    }

}


