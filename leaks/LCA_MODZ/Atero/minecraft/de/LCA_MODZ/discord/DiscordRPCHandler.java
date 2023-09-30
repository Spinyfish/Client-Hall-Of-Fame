package de.LCA_MODZ.discord;


import de.verschwiegener.atero.Management;

public class DiscordRPCHandler {
	
	public static final DiscordRPCHandler instance = new DiscordRPCHandler();
	private DiscordRPC discordRPC = new DiscordRPC();
	public static String second = Management.instance.CLIENT_NAME + " " + Management.instance.CLIENT_VERSION + " by LCA_MODZ";
	
	public void init() {
		this.discordRPC.start();
	}
	
	public void shutdown() {
		this.discordRPC.shutdown();
	}
	
	public DiscordRPC getDiscordRPC() {
		return this.discordRPC;
	}
}
