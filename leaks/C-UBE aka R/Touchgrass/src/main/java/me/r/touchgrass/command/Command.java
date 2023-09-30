package me.r.touchgrass.command;

import me.r.touchgrass.touchgrass;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

/**
 * Created by r on 13/03/2021
 */
public abstract class Command {

    public abstract void execute(String[] args);

    public abstract String getName();
    public abstract String getSyntax();
    public abstract String getDesc();

    public static void msg(String msg) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(touchgrass.prefix + "§7 " + msg));
    }
}
