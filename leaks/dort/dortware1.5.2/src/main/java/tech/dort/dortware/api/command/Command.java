package tech.dort.dortware.api.command;

import net.minecraft.client.Minecraft;

public abstract class Command {

    private final CommandData data;

    protected static final Minecraft mc = Minecraft.getMinecraft();

    public Command(CommandData data) {
        this.data = data;
    }

    public abstract void run(String command, String... args);

    public final CommandData getData() {
        return data;
    }

}
