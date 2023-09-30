package me.wavelength.baseclient.utils;

import me.wavelength.baseclient.event.events.PacketReceivedEvent;
import me.wavelength.baseclient.event.events.PacketSentEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class Utils {
	public static String message;
	
	public static void print() {

		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(" §9Zyth§7 >§f " + message));
	}
	
    public static void hypixelDamage() {
        for(int i = 0; i < 50; i++) {
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + 0.0625, Minecraft.getMinecraft().thePlayer.posZ, false));
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ, false));
        }
        Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ, true));
    }
    
    public static void spartanDamage() {
        for(int i = 0; i < 8; i++) {
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + 0.42, Minecraft.getMinecraft().thePlayer.posZ, false));
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ, false));
        }
        Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ, true));
    }
    
    
    public static void verusdamage() {
        Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + 3.05, Minecraft.getMinecraft().thePlayer.posZ, false));
        Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ, false));
        Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY+0.41999998688697815, Minecraft.getMinecraft().thePlayer.posZ, true));
    }
    
    public static void verusc08() {
    	Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY - 1.5, Minecraft.getMinecraft().thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(Minecraft.getMinecraft().theWorld, new BlockPos(-1, -1, -1))), 0, 0.94f, 0));
    }
    

}
