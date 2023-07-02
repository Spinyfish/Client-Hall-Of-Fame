package me.eldodebug.soar.utils.server;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;

public class ServerUtils {
	
	private static Minecraft mc = Minecraft.getMinecraft();
	
	public static boolean isOnServer() {
		if(mc.getCurrentServerData() != null) {
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isHypixel() {
		if(isOnServer() && mc.getCurrentServerData().serverIP.contains("hypixel")) {
			return true;
		}else {
			return false;
		}
	}
	
	public static String getServerIP() {
		
		String ip;
		
		if(mc.getCurrentServerData() != null) {
			ip = mc.getCurrentServerData().serverIP;
		}else {
			ip = "SinglePlayer";
		}
		
		return ip;
	}
	
	public static boolean isInTablist(Entity entity) {
		
		if(ServerUtils.isOnServer()) {
			for (Object item : mc.getNetHandler().getPlayerInfoMap()) {
				NetworkPlayerInfo playerInfo = (NetworkPlayerInfo) item;

				if (playerInfo != null && playerInfo.getGameProfile() != null && playerInfo.getGameProfile().getName().contains(entity.getName())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static int getPing() {
		if(mc.isSingleplayer()) {
			return 0;
		}else {
			return (int) mc.getCurrentServerData().pingToServer;
		}
	}
}
