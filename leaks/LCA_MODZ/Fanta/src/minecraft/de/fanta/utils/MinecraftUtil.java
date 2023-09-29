package de.fanta.utils;

import java.net.HttpURLConnection;
import java.net.URL;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

public interface MinecraftUtil {
    Minecraft mc = Minecraft.getMinecraft();
    WorldClient world = mc.theWorld;
    default EntityPlayerSP getPlayer() { return mc.thePlayer; };
  
    
    public default void resetRotation(float yaw) {
    	getPlayer().rotationYaw = yaw - yaw % 360 + getPlayer().rotationYaw % 360;
    }
    
    
}



