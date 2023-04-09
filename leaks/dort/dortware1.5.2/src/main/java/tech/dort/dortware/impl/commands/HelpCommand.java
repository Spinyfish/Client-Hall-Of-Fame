package tech.dort.dortware.impl.commands;

import tech.dort.dortware.Client;
import tech.dort.dortware.api.command.Command;
import tech.dort.dortware.api.command.CommandData;
import tech.dort.dortware.impl.utils.player.ChatUtil;

public class HelpCommand extends Command {
    public HelpCommand() {
        super(new CommandData("help"));
    }

    @Override
    public void run(String command, String... args) {
        if ("help".equals(command)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Commands: \n");
            for (Command c : Client.INSTANCE.getCommandManager().getObjects()) {
                stringBuilder.append("Command: (").append(c.getData().getName());
                for (String alias : c.getData().getAliases()) {
                    stringBuilder.append(", ").append(alias);
                }
                stringBuilder.append(")\n");
            }
            ChatUtil.displayChatMessage(stringBuilder.toString());
        }
    }
}
