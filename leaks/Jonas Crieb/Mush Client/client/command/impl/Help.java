package client.command.impl;

import client.Client;
import client.command.Command;
import client.util.chat.ChatUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public final class Help extends Command {

    public Help() {
        super("Gives you a list of all commands", "help", "?");
    }

    @Override
    public void execute(final String[] args) {
        Client.INSTANCE.getCommandManager()
                .forEach(command -> ChatUtil.display(StringUtils.capitalize(command.getExpressions()[0]) + " " + Arrays.toString(command.getExpressions()) + " \2478» \2477" + command.getDescription()));
    }
}
