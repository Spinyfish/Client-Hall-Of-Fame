package net.minecraft.command;

public class PlayerNotFoundException extends CommandException {
    public PlayerNotFoundException() {
        this("commands.generic.player.notFound");
    }

    public PlayerNotFoundException(final String message, final Object... replacements) {
        super(message, replacements);
    }
}
