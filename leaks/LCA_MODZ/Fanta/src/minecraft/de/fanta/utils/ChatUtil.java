package de.fanta.utils;

import de.fanta.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;

public class ChatUtil {

    public static void sendChatMessage(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
    }

    public static void sendChatMessageWithPrefix(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§7[§f" + Client.INSTANCE.name + "§7]§f " + message));
    }

    public static void messageWithoutPrefix(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
    }

    public static void sendChatInfo(String string) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§7[§f" + Client.INSTANCE.name  + "§7]§a " + string));
    }

    public static void sendChatError(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§7[§f" + Client.INSTANCE.name  + "§7]§c " + message));
    }
}
