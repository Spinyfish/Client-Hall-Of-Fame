package tech.dort.dortware.impl.commands;

import tech.dort.dortware.Client;
import tech.dort.dortware.api.command.Command;
import tech.dort.dortware.api.command.CommandData;
import tech.dort.dortware.impl.utils.player.ChatUtil;

public class SetSpectatorAltCommand extends Command {
    public SetSpectatorAltCommand() {
        super(new CommandData("setspectator"));
    }

    @Override
    public void run(String command, String... args) {
        if ("setspectator".equalsIgnoreCase(command) && args.length > 0) {
            Client.setAlt(args[1]);
            ChatUtil.displayChatMessage("Set account");
        }
    }
}
