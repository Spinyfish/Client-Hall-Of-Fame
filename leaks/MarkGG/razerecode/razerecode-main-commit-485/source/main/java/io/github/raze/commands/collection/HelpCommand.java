package io.github.raze.commands.collection;

import io.github.raze.Raze;
import io.github.raze.commands.system.BaseCommand;
import io.github.raze.utilities.collection.visual.ChatUtil;

public class HelpCommand extends BaseCommand {

    public HelpCommand() {
        super("Help", "View the client's information", "help", "h");
    }

    public String onCommand(String[] arguments, String command) {
        ChatUtil.addChatMessage("", false);
        ChatUtil.addChatMessage("Version " + Raze.INSTANCE.getVersion());
        ChatUtil.addChatMessage("", false);

        for(BaseCommand command1 : Raze.INSTANCE.MANAGER_REGISTRY.COMMAND_REGISTRY.getCommands()) {
            ChatUtil.addChatMessage(Raze.INSTANCE.getPrefix() + command1.syntax + " - " + command1.description);
        }

        return "";
    }

}
