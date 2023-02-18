/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.arikia.dev.drpc.DiscordRPC
 *  vip.astroline.client.storage.utils.other.DiscordThread
 */
package vip.astroline.client.storage.utils.other;

import net.arikia.dev.drpc.DiscordRPC;
import vip.astroline.client.storage.utils.other.DiscordThread;

/*
 * Exception performing whole class analysis ignored.
 */
class DiscordThread.1
extends Thread {
    DiscordThread.1(String x0) {
        super(x0);
    }

    @Override
    public void run() {
        while (DiscordThread.access$000((DiscordThread)DiscordThread.this)) {
            DiscordRPC.discordRunCallbacks();
        }
    }
}
