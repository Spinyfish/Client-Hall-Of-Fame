package com.alan.clients.module.impl.combat.velocity;

import com.alan.clients.component.impl.player.PingSpoofComponent;
import com.alan.clients.module.impl.combat.Velocity;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.value.Mode;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

import java.util.concurrent.ConcurrentLinkedQueue;

public final class DelayVelocity extends Mode<Velocity> {

    private final NumberValue horizontal = new NumberValue("Horizontal", this, 100, 0, 100, 1);
    private final NumberValue vertical = new NumberValue("Vertical", this, 100, 0, 100, 1);
    private final NumberValue delay = new NumberValue("Delay", this, 10, 1, 50, 1);
    private final BooleanValue pingSpoof = new BooleanValue("Ping Spoof", this, true);

    private final ConcurrentLinkedQueue<PacketUtil.TimedPacket> velocityPackets = new ConcurrentLinkedQueue<>();

    public DelayVelocity(String name, Velocity parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {

        for (final PacketUtil.TimedPacket p : velocityPackets) {
            if (System.currentTimeMillis() > p.getTime() + delay.getValue().longValue() * 50) {
                PacketUtil.receiveNoEvent(p.getPacket());
                velocityPackets.remove(p);
            }
        }

        if (pingSpoof.getValue() && !velocityPackets.isEmpty()) {
            PingSpoofComponent.setSpoofing(delay.getValue().intValue() * 50, true, false, false, false, false);
        }
    };

    @EventLink()
    public final Listener<PacketReceiveEvent> onPacketReceiveEvent = event -> {
        if (getParent().onSwing.getValue() || getParent().onSprint.getValue() && !mc.thePlayer.isSwingInProgress) return;

        final Packet<?> p = event.getPacket();

        final double horizontal = this.horizontal.getValue().doubleValue();
        final double vertical = this.vertical.getValue().doubleValue();

        if (p instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) p;

            if (wrapper.getEntityID() == mc.thePlayer.getEntityId()) {
                wrapper.motionX *= horizontal / 100;
                wrapper.motionY *= vertical / 100;
                wrapper.motionZ *= horizontal / 100;

                velocityPackets.add(new PacketUtil.TimedPacket(p, System.currentTimeMillis()));
                event.setCancelled(true);
            }
        } else if (p instanceof S27PacketExplosion) {
            final S27PacketExplosion wrapper = (S27PacketExplosion) p;

            wrapper.posX *= horizontal / 100;
            wrapper.posY *= vertical / 100;
            wrapper.posZ *= horizontal / 100;

            velocityPackets.add(new PacketUtil.TimedPacket(p, System.currentTimeMillis()));
            event.setCancelled(true);
        }
    };
}