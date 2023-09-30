package dev.africa.pandaware.manager.discord;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.interfaces.Initializable;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class DiscordRP implements Initializable {
    private long startTime = 0;

    @Override
    public void init() {
        this.startTime = System.currentTimeMillis();

        final DiscordEventHandlers discordEventHandlers = new DiscordEventHandlers.Builder()
                .setReadyEventHandler(discordUser -> {
                    final String firstLine = "Launching " + Client.getInstance().getManifest().getClientName() +
                            Client.getInstance().getManifest().getClientVersion() + ".";

                    this.updateStatus(firstLine, "...Yep, there's RPC now.");
                }).build();

        DiscordRPC.discordInitialize("980137797866360912", discordEventHandlers, true);

        new Thread("Discord RPC Callback") {
            @Override
            public void run() {
                DiscordRPC.discordRunCallbacks();
            }
        }.start();
    }

    public void updateStatus(String lineOne, String lineTwo) {
//        final DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder(lineTwo)
//                .setBigImage("large", Client.getInstance().getRandomTitleText())
//                .setDetails(lineOne)
//                .setStartTimestamps(this.startTime);
//
//        DiscordRPC.discordUpdatePresence(builder.build());
    }

    public void shutdown() {
        DiscordRPC.discordShutdown();
    }
}
