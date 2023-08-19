/**
 * @project hakarware
 * @author CodeMan
 * @at 25.07.23, 20:34
 */

package cc.swift.commands.impl;

import cc.swift.Swift;
import cc.swift.commands.Command;
import cc.swift.util.ChatUtil;

import java.util.ArrayList;

public final class HelpCommand extends Command {
    public HelpCommand() {
        super("Help", "help", new String[] {"h"});
    }

    @Override
    public void onCommand(String[] args) {
        ArrayList<String> sent = new ArrayList<>();
        for (Command command : Swift.INSTANCE.getCommandManager().getCommands()) {
            if (sent.contains(command.name)) continue;
            ChatUtil.printChatMessage(command.name + " - " + command.description);
            sent.add(command.name);
        }
    }
}
