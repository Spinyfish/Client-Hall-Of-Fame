package com.alan.clients.module.impl.other;

import com.alan.clients.Client;
import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.TickEvent;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

@Rise
@ModuleInfo(name = "module.other.richpresence.name", description = "module.other.richpresence.description", category = Category.OTHER, autoEnabled = true)
public final class RichPresence extends Module {

    private boolean started;

    @EventLink()
    public final Listener<TickEvent> onTick = event -> {

        if (!started) {
            final DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder("") {{
                setDetails("Rise " + Client.VERSION_FULL);
                setBigImage("rise", "");
                setStartTimestamps(System.currentTimeMillis());
            }};

            DiscordRPC.discordUpdatePresence(builder.build());

            final DiscordEventHandlers handlers = new DiscordEventHandlers();
            DiscordRPC.discordInitialize("1030003698685968485", handlers, true);

            new Thread(() -> {
                while (this.isEnabled()) {
                    DiscordRPC.discordRunCallbacks();
                }
            }, "Discord RPC Callback").start();

            started = true;
        }
    };

    @Override
    protected void onDisable() {
        DiscordRPC.discordShutdown();
        started = false;
    }
}
