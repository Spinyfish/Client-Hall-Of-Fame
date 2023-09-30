package io.github.raze.commands.collection;

import io.github.raze.Raze;
import io.github.raze.commands.system.BaseCommand;
import io.github.raze.utilities.collection.configs.PrefixUtil;

import java.util.Arrays;

public class PrefixCommand extends BaseCommand {

    public PrefixCommand() {
        super("Prefix", "Set the chat prefix", "prefix <Character>", "p");
    }

    public String onCommand(String[] arguments, String command) {
        if (arguments.length == 1) {
            return "Usage: " + getSyntax();
        }

        Raze.INSTANCE.prefix = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
        PrefixUtil.savePrefix(String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length)));

        return String.format("Set the prefix to %s", Raze.INSTANCE.getPrefix());
    }

}
