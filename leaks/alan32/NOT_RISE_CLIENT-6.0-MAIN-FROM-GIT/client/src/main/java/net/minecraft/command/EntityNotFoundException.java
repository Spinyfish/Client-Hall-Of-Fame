package net.minecraft.command;

public class EntityNotFoundException extends CommandException {
    public EntityNotFoundException() {
        this("commands.generic.entity.notFound");
    }

    public EntityNotFoundException(final String p_i46035_1_, final Object... p_i46035_2_) {
        super(p_i46035_1_, p_i46035_2_);
    }
}
