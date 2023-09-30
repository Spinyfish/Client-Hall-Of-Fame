package dev.tenacity.commands;

import dev.tenacity.utils.player.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

public abstract class Command {
    protected final Minecraft mc = Minecraft.getMinecraft();
    private final String name, description, usage;
    private final String[] otherPrefixes;
    public static boolean sendSuccess = true;

    public Command(String name, String description, String usage, String... otherPrefixes) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.otherPrefixes = otherPrefixes;
    }

    public abstract void execute(String[] args);

    public String getName() {
        return name;
    }

    public void sendChatWithPrefix(String message) {
        if (sendSuccess)
            ChatUtil.print(message);
    }

    public void sendChatError(String message) {
        ChatUtil.print("Error", EnumChatFormatting.RED, message);
    }

    public void sendChatWithInfo(String message) {
        if (sendSuccess)
            ChatUtil.print("Info", EnumChatFormatting.BLUE, message);
    }

    public void usage() {
        ChatUtil.error("Usage: " + usage);
    }

    public String getUsage() {
        return this.usage;
    }

    public String getDescription() {
        return description;
    }

    public String[] getOtherPrefixes() {
        return otherPrefixes;
    }
}
