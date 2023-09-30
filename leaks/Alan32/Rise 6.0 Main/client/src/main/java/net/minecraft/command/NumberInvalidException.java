package net.minecraft.command;

public class NumberInvalidException extends CommandException {
    public NumberInvalidException() {
        this("commands.generic.num.invalid");
    }

    public NumberInvalidException(final String message, final Object... replacements) {
        super(message, replacements);
    }
}
