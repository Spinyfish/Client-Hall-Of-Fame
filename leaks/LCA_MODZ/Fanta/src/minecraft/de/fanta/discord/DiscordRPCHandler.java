package de.fanta.discord;

import de.fanta.Client;

public class DiscordRPCHandler {
	
	public static final DiscordRPCHandler instance = new DiscordRPCHandler();
	private DiscordRPC discordRPC = new DiscordRPC();
	public static String second = Client.INSTANCE.getName() + " " + Client.INSTANCE.getVersion()+ " by LCA_MODZ";
	
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
