/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.arikia.dev.drpc.DiscordEventHandlers
 *  net.arikia.dev.drpc.DiscordEventHandlers$Builder
 *  net.arikia.dev.drpc.DiscordRPC
 *  net.arikia.dev.drpc.DiscordRichPresence
 *  net.arikia.dev.drpc.DiscordRichPresence$Builder
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.service.module.Module
 */
package vip.astroline.client.storage.utils.other;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import vip.astroline.client.Astroline;
import vip.astroline.client.service.module.Module;

public class DiscordThread
extends Thread {
    private boolean running = true;
    private static long created = 0L;
    private String app_id = "1055788885000585216";

    public static int getEnabledModules() {
        return (int)Astroline.INSTANCE.moduleManager.getModules().stream().filter(Module::isToggled).count();
    }

    public static int getTotalModules() {
        return Astroline.INSTANCE.moduleManager.getModules().size();
    }

    @Override
    public void start() {
        created = System.currentTimeMillis();
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(user -> DiscordThread.update("Launching...")).build();
        DiscordRPC.discordInitialize((String)this.app_id, (DiscordEventHandlers)handlers, (boolean)true);
        new /* Unavailable Anonymous Inner Class!! */.start();
    }

    public void shutdown() {
        this.running = false;
        DiscordRPC.discordShutdown();
    }

    public static void update(String status) {
        DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(status);
        b.setBigImage("logo", "Astroline " + Astroline.INSTANCE.VERSION + " [BETA]");
        b.setDetails("Enabled Modules: " + DiscordThread.getEnabledModules() + " / " + DiscordThread.getTotalModules());
        b.setStartTimestamps(created);
        DiscordRPC.discordUpdatePresence((DiscordRichPresence)b.build());
    }

    static /* synthetic */ boolean access$000(DiscordThread x0) {
        return x0.running;
    }
}
