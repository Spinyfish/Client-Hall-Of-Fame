package net.minecraft.command;

public class CommandNotFoundException extends CommandException {
    public CommandNotFoundException() {
        this("commands.generic.notFound");
    }

    public CommandNotFoundException(final String p_i1363_1_, final Object... p_i1363_2_) {
        super(p_i1363_1_, p_i1363_2_);
    }
}
