package me.jinthium.straight.impl.utils;

import me.jinthium.scripting.api.ScriptModule;
import me.jinthium.straight.api.util.MinecraftInstance;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.io.File;

public class ChatUtil implements MinecraftInstance {

    public static void print(boolean prefix, String message) {
        if (mc.thePlayer != null) {
            if (prefix) message = "§7[§3Straightware§r§7] " + message;
            mc.thePlayer.addChatMessage(new ChatComponentText(message));
        }
    }

    public static void error(String message) {
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText("§7[§cERROR§r§7] " + message));
        }
    }

    public static void scriptError(ScriptModule scriptFile, String message) {
        if (mc.thePlayer != null) {
            if (scriptFile.getFile() == null) {
                mc.thePlayer.addChatMessage(new ChatComponentText("§3SCRIPT ERROR (Cloud Script): §r§f" + scriptFile.getName() + " §c" + message));
            } else {
                mc.thePlayer.addChatMessage(new ChatComponentText("§3SCRIPT ERROR: §r§f" + scriptFile.getFile().getName() + " §c" + message));
            }
        }
    }

    public static void scriptError(File scriptFile, String message) {
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText("§3SCRIPT ERROR: §r§f" + scriptFile.getName() + " §c" + message));
        }
    }

    public static void print(String prefix, EnumChatFormatting color, String message) {
        if (mc.thePlayer != null) {
            message = "§7[§" + color.getFormattingCode() + "" + prefix.toUpperCase() + "§r§7]§r §" + color.getFormattingCode() + message;
            mc.thePlayer.addChatMessage(new ChatComponentText(message));
        }
    }

    public static void print(Object o) {
        print(true, String.valueOf(o));
    }

    public static void send(String message) {
        if (mc.thePlayer != null) {
            mc.thePlayer.sendChatMessage(message);
        }
    }

    public static void printSystem(Object message){
        System.out.println(message);
    }

}