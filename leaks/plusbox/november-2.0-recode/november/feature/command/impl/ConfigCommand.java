package lol.november.feature.command.impl;

import lol.november.November;
import lol.november.feature.command.Command;
import lol.november.feature.command.exceptions.CommandInvalidSyntaxException;

public class ConfigCommand extends Command {
    @Override
    public void dispatch(String[] args) throws Exception {
        if (args.length == 0) throw new CommandInvalidSyntaxException(
                "config action"
        );

        if (args.length == 1) throw new CommandInvalidSyntaxException(
                "config name"
        );

        if (args[0].equals("save")) {
            November.instance().configs().load();
        }
    }
}
