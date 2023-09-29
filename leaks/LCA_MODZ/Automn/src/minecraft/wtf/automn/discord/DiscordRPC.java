package wtf.automn.discord;


import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;
import net.minecraft.client.Minecraft;
import wtf.automn.Automn;

public class DiscordRPC {
    private boolean running = true;
    private long created = 0L;

    public void start() {
        this.created = System.currentTimeMillis();

        final DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(new ReadyCallback() {
            @Override
            public void apply(final DiscordUser user) {
                final Minecraft mc = Minecraft.getMinecraft();

                DiscordRPC.this.update("Automn", " " + Automn.BUILD);


                if (user.userId.equalsIgnoreCase("946069348102074389")) {


                }
            }
        }).build();
        net.arikia.dev.drpc.DiscordRPC.discordInitialize("946069348102074389", handlers, true);
        new Thread("Discord RPC Callback") {
            @Override
            public void run() {
                while (DiscordRPC.this.running)
                    net.arikia.dev.drpc.DiscordRPC.discordRunCallbacks();
            }
        }.start();
    }

    public void shutdown() {
        this.running = false;
        net.arikia.dev.drpc.DiscordRPC.discordShutdown();
    }

    public void update(final String first, final String second) {
        final DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(second);
        b.setBigImage("", "");
        b.setDetails(first);
        b.setStartTimestamps(this.created);

        net.arikia.dev.drpc.DiscordRPC.discordUpdatePresence(b.build());
    }
}
