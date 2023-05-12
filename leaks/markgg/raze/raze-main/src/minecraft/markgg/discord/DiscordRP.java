package markgg.discord;

import markgg.Client;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;

public class DiscordRP {

	public boolean running = true;
	private long created = 0;

	public void start() {
		this.created = System.currentTimeMillis();

		DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(new ReadyCallback() {
			
			@Override
			public void apply(DiscordUser user) {
				update("Launching Raze...", "");
			}
		}).build();

		DiscordRPC.discordInitialize("1091013564376039524", handlers, true);

		new Thread("Discord RPC Callback") {
				
			@Override
			public void run() {
				while(running)
					DiscordRPC.discordRunCallbacks();
			}
		}.start();
	}

	public void shutdown() {
		running = false;
		DiscordRPC.discordShutdown();
	}

	public void update(String _1stLine, String _2ndLine) {
		DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(_2ndLine);
		b.setBigImage("large", "");
		b.setDetails(_1stLine);
		b.setStartTimestamps(created);

		DiscordRPC.discordUpdatePresence(b.build());
	}

}
