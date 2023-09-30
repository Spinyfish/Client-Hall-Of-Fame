/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.commands.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import Reality.Realii.Client;
import Reality.Realii.commands.Command;
import Reality.Realii.utils.cheats.player.Helper;

public class Help
        extends Command {
    public Help() {
        super("Help", new String[]{"list"}, "", "get some help information");
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 0) {
            Helper.sendMessageWithoutPrefix("\u00a77\u00a7m\u00a7l----------------------------------");
            Helper.sendMessageWithoutPrefix("                    \u00a7b\u00a7lReality Client");
            for (Command command : Client.instance.getCommandManager().getCommands()) {
                if(!command.getHelp().equals("Setup this module"))
                    Helper.sendMessageWithoutPrefix(ChatFormatting.BLUE + "[Command] ." + command.getName() + " " + command.getSyntax() + " ->" + command.getHelp());
            }

            Helper.sendMessageWithoutPrefix("\u00a77\u00a7m\u00a7l----------------------------------");
        } else {
            Helper.sendMessage("invalid syntax Valid .help");
        }
        return null;
    }
}

