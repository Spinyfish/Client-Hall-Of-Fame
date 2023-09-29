package frapppyz.cutefurry.pics.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;

import java.awt.*;
import java.util.ArrayList;

public class Logger {

    public void warning(String message){
        System.out.println("\u001B[33m" + "[Astra] " + "\u001B[0m" + message);
    }
    public void addChat(String message){ Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.AQUA + "[Astra] " + ChatFormatting.WHITE + message)); }

    public void error(String message){
        System.out.println("\u001B[31m" + "[Astra] " + "\u001B[0m" + message);
    }

    public void info(String message){
        System.out.println("\u001B[36m" + "[Astra] " + "\u001B[0m" + message);
    }
}
