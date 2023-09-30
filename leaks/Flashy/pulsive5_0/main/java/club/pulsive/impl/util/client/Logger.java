package club.pulsive.impl.util.client;

import club.pulsive.api.minecraft.MinecraftUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class Logger implements MinecraftUtil {
    
    public static void print(String message){
        mc.thePlayer.addChatMessage(new ChatComponentText("[Pulsive] " + message));
    }
    
    public static void sayShitInChat(String message){
        mc.thePlayer.sendChatMessage(message);
    }
    
    public static void printSysLog(String message){
        System.out.println("[Pulsive] " + message);
    }
    
}
