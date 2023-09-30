package net.minecraft.command;

public class WrongUsageException extends SyntaxErrorException {
    public WrongUsageException(final String message, final Object... replacements) {
        super(message, replacements);
    }
}
