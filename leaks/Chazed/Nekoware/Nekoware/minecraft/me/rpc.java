package me;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;

public class rpc {
	
	private boolean running = true;
	private long created = 0;
	
	public static void start() {
		DiscordRPC.discordInitialize("1001238007787032616", new DiscordEventHandlers(), true);
		String nigga = Minecraft.getMinecraft().thePlayer == null ? "Null" : Minecraft.getMinecraft().thePlayer.getName();
		DiscordRichPresence rich = new DiscordRichPresence.Builder("Made By FlowaSkid, Boffa, & Hello_SW").setDetails("Name: " + nigga).setBigImage("fullb", "").setSmallImage("a", "").build();
		DiscordRPC.discordUpdatePresence(rich);
	}
	
	public void shutdown() {
		
	}

	public void update(String firstline, String secondline) {
		
	}
}
