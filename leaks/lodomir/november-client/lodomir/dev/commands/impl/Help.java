/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 */
package lodomir.dev.commands.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ListIterator;
import lodomir.dev.November;
import lodomir.dev.commands.Command;

public class Help
extends Command {
    public Help() {
        super("Help", "Lists the name of modules", "help", "help");
    }

    @Override
    public void onCommand(String[] args, String command) {
        November.Log("All available commands:");
        ListIterator<Command> var2 = November.INSTANCE.commandManager.commands.listIterator();
        while (var2.hasNext()) {
            Command cmd = (Command)var2.next();
            November.Log(ChatFormatting.WHITE + cmd.getName() + " " + ChatFormatting.GRAY + cmd.aliases + " - " + cmd.getDescription());
        }
    }
}

