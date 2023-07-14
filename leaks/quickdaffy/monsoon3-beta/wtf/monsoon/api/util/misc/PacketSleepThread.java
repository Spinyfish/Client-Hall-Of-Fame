/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.util.misc;

import net.minecraft.network.Packet;
import wtf.monsoon.api.util.misc.PacketUtil;

public class PacketSleepThread
extends Thread {
    private final Packet<?> packet;
    private final long delay;

    public PacketSleepThread(Packet<?> packet, long delay) {
        this.packet = packet;
        this.delay = delay;
    }

    @Override
    public void run() {
        try {
            PacketSleepThread.sleep(this.delay);
            PacketUtil.sendPacketNoEvent(this.packet);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

