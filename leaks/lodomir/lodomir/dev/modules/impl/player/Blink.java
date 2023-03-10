/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.event.impl.network.EventSendPacket;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;

public class Blink
extends Module {
    private final ArrayList<Packet> packets = new ArrayList();

    public Blink() {
        super("Blink", 0, Category.PLAYER);
    }

    @Override
    @Subscribe
    public void onSendPacket(EventSendPacket event) {
        if (event.getPacket() instanceof C02PacketUseEntity || event.getPacket() instanceof C0APacketAnimation || event.getPacket() instanceof C03PacketPlayer || event.getPacket() instanceof C07PacketPlayerDigging || event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            this.packets.add(event.getPacket());
            event.setCancelled(true);
        }
    }

    @Override
    @Subscribe
    public void onGetPacket(EventGetPacket event) {
    }

    @Override
    public void onDisable() {
        for (Packet p : this.packets) {
            if (mc.isSingleplayer()) continue;
            this.sendPacketSilent(p);
        }
        this.packets.clear();
        super.onDisable();
    }

    @Override
    public void onEnable() {
        this.packets.clear();
        super.onEnable();
    }
}

