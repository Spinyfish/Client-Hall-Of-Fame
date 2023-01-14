package com.thunderware.utils.api;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;

public class DiscordAPI
{
    public static class RPC {
        private boolean running = true;
        private long created = 0;

        public void start() {
            this.created = System.currentTimeMillis();

            DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(new ReadyCallback() {
                @Override
                public void apply(DiscordUser user) {
                    System.out.println("Logged in as " + user.username + "#" + user.discriminator);
                    update("Starting...", "", "", "");
                }
            }).build();

            DiscordRPC.discordInitialize("944981472434090035", handlers, true);

            new Thread("Discord RPC Callback") {
                @Override
                public void run() {
                    while (running) {
                        DiscordRPC.discordRunCallbacks();
                    }
                }
            }.start();
        }

        public void shutdown() {
            running = false;
            DiscordRPC.discordShutdown();
        }

        public void update(String firstLine, String secondLine, String smallImage, String smallImageText) {
            DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(secondLine);
            b.setBigImage("rld", "our logo isn't stolen shut up");
            if (!smallImage.isEmpty())
                b.setSmallImage(smallImage, smallImageText);
            b.setDetails(firstLine);
            b.setStartTimestamps(created);

            DiscordRPC.discordUpdatePresence(b.build());
        }
    }
}

