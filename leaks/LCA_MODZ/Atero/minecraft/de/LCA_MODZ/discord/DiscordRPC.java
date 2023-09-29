package de.LCA_MODZ.discord;


import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;
import net.minecraft.client.Minecraft;

public class DiscordRPC {
	private boolean running = true;
	private long created = 0L;

	public void start() {
		this.created = System.currentTimeMillis();

		DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(new ReadyCallback() {
			public void apply(DiscordUser user) {
				Minecraft mc = Minecraft.getMinecraft();

					DiscordRPC.this.update("Atero", "A Client for all Anticheats");





				if (user.userId.equalsIgnoreCase("733050731816288306")) {

					
				}
			}
		}).build();
		net.arikia.dev.drpc.DiscordRPC.discordInitialize("733050731816288306", handlers, true);
		new Thread("Discord RPC Callback") {
			public void run() {
				while (DiscordRPC.this.running) {
					net.arikia.dev.drpc.DiscordRPC.discordRunCallbacks();
				}
			}
		}.start();
	}

	public void shutdown() {
		this.running = false;
		net.arikia.dev.drpc.DiscordRPC.discordShutdown();
	}

	public void update(String first, String second) {
		DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(second);
		b.setBigImage("sadadadad", "");
		b.setDetails(first);
		b.setStartTimestamps(this.created);

		net.arikia.dev.drpc.DiscordRPC.discordUpdatePresence(b.build());
	}
}
