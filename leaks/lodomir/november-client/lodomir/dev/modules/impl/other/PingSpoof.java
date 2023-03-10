/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.other;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.event.impl.network.EventSendPacket;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.utils.math.TimeUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class PingSpoof
extends Module {
    public NumberSetting ping = new NumberSetting("Ping", 0.0, 5000.0, 300.0, 50.0);
    private final ArrayList<Packet> packets = new ArrayList();
    private Packet sendPacket = null;
    private final TimeUtils timer = new TimeUtils();

    public PingSpoof() {
        super("PingSpoof", 0, Category.OTHER);
        this.addSetting(this.ping);
    }

    @Override
    public void onEnable() {
        this.packets.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.packets.clear();
        super.onDisable();
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        this.setSuffix(String.valueOf(this.ping.getValue()));
        if (mc.isSingleplayer()) {
            return;
        }
        if (this.timer.hasReached((long)this.ping.getValue()) && this.packets.size() >= 1) {
            this.sendPacket = this.packets.get(0);
            this.sendPacket(this.packets.get(0));
            this.packets.remove(0);
        }
        this.timer.reset();
    }

    @Override
    @Subscribe
    public void onGetPacket(EventGetPacket event) {
        if (mc.isSingleplayer()) {
            return;
        }
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            this.timer.reset();
        }
    }

    @Override
    @Subscribe
    public void onSendPacket(EventSendPacket e) {
        if (mc.isSingleplayer()) {
            return;
        }
        Packet p = e.getPacket();
        if (p instanceof C0FPacketConfirmTransaction || p instanceof C00PacketKeepAlive) {
            this.packets.add((int)((double)System.currentTimeMillis() + this.ping.getValue()), p);
            e.setCancelled(true);
        }
    }
}

