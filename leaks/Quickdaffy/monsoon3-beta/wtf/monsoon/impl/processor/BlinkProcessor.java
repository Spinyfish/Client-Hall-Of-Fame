/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.processor;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.monsoon.api.processor.Processor;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;

public class BlinkProcessor
extends Processor {
    private final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue();
    private boolean blinking;
    private boolean dispatch;
    @EventLink
    public final Listener<EventPacket> onPacketSend = event -> {
        if (this.mc.thePlayer == null) {
            this.packets.clear();
            return;
        }
        if (this.mc.thePlayer.isDead || this.mc.isSingleplayer()) {
            this.packets.forEach((Consumer<Packet<?>>)((Consumer<Packet>)PacketUtil::sendPacketNoEvent));
            this.packets.clear();
            this.blinking = false;
            return;
        }
        Packet packet = event.getPacket();
        if (packet instanceof C03PacketPlayer) {
            if (this.blinking && !this.dispatch) {
                this.packets.add(packet);
                event.setCancelled(true);
            } else {
                this.packets.forEach((Consumer<Packet<?>>)((Consumer<Packet>)PacketUtil::sendPacketNoEvent));
                this.packets.clear();
                this.dispatch = false;
            }
        }
    };
    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = event -> {
        if (this.mc.thePlayer.ticksExisted <= 1) {
            this.packets.clear();
            this.blinking = false;
        }
    };

    public void dispatch() {
        this.dispatch = true;
    }

    public ConcurrentLinkedQueue<Packet<?>> getPackets() {
        return this.packets;
    }

    public boolean isBlinking() {
        return this.blinking;
    }

    public boolean isDispatch() {
        return this.dispatch;
    }

    public Listener<EventPacket> getOnPacketSend() {
        return this.onPacketSend;
    }

    public Listener<EventPreMotion> getEventPreMotionListener() {
        return this.eventPreMotionListener;
    }

    public void setBlinking(boolean blinking) {
        this.blinking = blinking;
    }

    public void setDispatch(boolean dispatch) {
        this.dispatch = dispatch;
    }
}

