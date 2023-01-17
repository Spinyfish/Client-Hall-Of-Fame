package com.alan.clients.module.impl.movement.inventorymove;

import com.alan.clients.module.impl.movement.InventoryMove;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.JumpEvent;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.newevent.impl.other.WorldChangeEvent;
import com.alan.clients.newevent.impl.packet.PacketSendEvent;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.value.impl.NumberValue;
import com.alan.clients.value.Mode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0EPacketClickWindow;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Auth
 */
public class BufferAbuseInventoryMove extends Mode<InventoryMove> {

    private final NumberValue clicksSetting = new NumberValue("Clicks", this, 3, 2, 10, 1);
    private final NumberValue amount = new NumberValue("Amount", this, 5, 1, 10, 1);

    private final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();

    private boolean done, sent;
    private int clicks;

    public BufferAbuseInventoryMove(String name, InventoryMove parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        if (this.canAbuse()) {
            if (!this.sent) {
                if (!this.done) {
                    this.done = true;
                } else {
                    for (int i = 0; i < this.amount.getValue().intValue(); i++) {
                        PacketUtil.sendNoEvent(new C0EPacketClickWindow());
                    }
                    packets.forEach(PacketUtil::sendNoEvent);
                    packets.clear();
                    this.sent = true;
                }
            }
        } else {
            this.done = false;
            this.sent = false;
        }
    };

    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {
        if (this.canAbuse() && !this.sent) {
            event.setCancelled(true);
        }
    };

    @EventLink()
    public final Listener<JumpEvent> onJump = event -> {
        if (this.canAbuse() && !this.sent) {
            event.setCancelled(true);
        }
    };

    @EventLink()
    public final Listener<PacketSendEvent> onPacketSend = event -> {

        final Packet<?> p = event.getPacket();

        if (p instanceof C0EPacketClickWindow) {
            if (this.canAbuse() && !this.sent) {
                event.setCancelled(true);
                packets.add(p);
                return;
            }
            this.clicks++;
        }
    };

    @EventLink()
    public final Listener<WorldChangeEvent> onWorldChange = event -> {
        packets.clear();
    };

    private boolean canAbuse() {
        return clicks > 0 && clicks % this.clicksSetting.getValue().intValue() == 0;
    }
}