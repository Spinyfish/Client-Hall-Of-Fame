/**
 * @project hakarware
 * @author CodeMan
 * @at 25.07.23, 23:34
 */

package cc.swift.commands.impl;

import cc.swift.commands.Command;
import cc.swift.util.ChatUtil;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.util.Session;

public final class LoginCommand extends Command {
    public LoginCommand() {
        super("login", "Login to your account", new String[] {"l"});
    }

    @Override
    public void onCommand(String[] args) {
        if (args.length < 1) {
            ChatUtil.printChatMessage("Usage: login <username> <password>");
            return;
        }

        if (args.length < 2) {
            ChatUtil.printChatMessage("Logged in with username " + args[0] + ". (Offline) Reconnect to apply.");
            mc.session = new Session(args[0], "", "", "mojang");
            return;
        }

        String username = args[0];
        String password = args[1];

        ChatUtil.printChatMessage("Logging in..");

        new Thread(() -> {
            MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
            try {
                MicrosoftAuthResult result = authenticator.loginWithCredentials(username, password);
                mc.session = new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "mojang");
                ChatUtil.printChatMessage("Logged in with username " + mc.session.getUsername() + ". Reconnect to apply.");
            } catch (Exception e) {
                ChatUtil.printChatMessage("Failed to login with username " + username);
                e.printStackTrace();
            }
        }).start();
    }
}
