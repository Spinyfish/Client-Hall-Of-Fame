package com.alan.clients.component.impl.player;

import com.alan.clients.api.Rise;
import com.alan.clients.component.Component;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.util.packet.PacketUtil;
import util.time.StopWatch;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;

import java.util.concurrent.ConcurrentLinkedQueue;

@Rise
public final class PingSpoofComponent extends Component {

    public static final ConcurrentLinkedQueue<PacketUtil.TimedPacket> packets = new ConcurrentLinkedQueue<>();
    private static final StopWatch stopWatch = new StopWatch();
    public static boolean spoofing;
    public static int delay;
    public static boolean normal, teleport, velocity, world, entity;

    @EventLink(value = Priorities.VERY_LOW)
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        for (final PacketUtil.TimedPacket packet : PingSpoofComponent.packets) {
            if (System.currentTimeMillis() > packet.getTime() + (PingSpoofComponent.spoofing ? PingSpoofComponent.delay : 0)) {
                try {
                    PacketUtil.receiveNoEvent(packet.getPacket());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                PingSpoofComponent.packets.remove(packet);
            }
        }

        if (stopWatch.finished(60)) {
            PingSpoofComponent.spoofing = false;

            for (final PacketUtil.TimedPacket packet : PingSpoofComponent.packets) {
                PacketUtil.receiveNoEvent(packet.getPacket());
                PingSpoofComponent.packets.remove(packet);
            }
        }
    };

    public static void setSpoofing(final int delay, final boolean normal, final boolean teleport,
                                   final boolean velocity, final boolean world, final boolean entity) {
        PingSpoofComponent.spoofing = true;
        PingSpoofComponent.delay = delay;
        PingSpoofComponent.normal = normal;
        PingSpoofComponent.teleport = teleport;
        PingSpoofComponent.velocity = velocity;
        PingSpoofComponent.world = world;
        PingSpoofComponent.entity = entity;

        stopWatch.reset();
    }

    @EventLink(value = Priorities.VERY_LOW)
    public final Listener<PacketReceiveEvent> onPacketReceiveEvent = event -> {

        final Packet<?> packet = event.getPacket();

        if (PingSpoofComponent.spoofing && mc.getNetHandler().doneLoadingTerrain) {
            if (((packet instanceof S32PacketConfirmTransaction || packet instanceof S00PacketKeepAlive) && normal) ||

                    ((packet instanceof S08PacketPlayerPosLook || packet instanceof S09PacketHeldItemChange) && teleport) ||

                    ((packet instanceof S12PacketEntityVelocity || packet instanceof S27PacketExplosion) && velocity) ||

                    ((packet instanceof S26PacketMapChunkBulk || packet instanceof S21PacketChunkData ||
                            packet instanceof S23PacketBlockChange || packet instanceof S22PacketMultiBlockChange) && world) ||

                    ((packet instanceof S13PacketDestroyEntities || packet instanceof S14PacketEntity ||
                            packet instanceof S18PacketEntityTeleport ||
                            packet instanceof S20PacketEntityProperties || packet instanceof S19PacketEntityHeadLook) && entity)) {

                packets.add(new PacketUtil.TimedPacket(packet, System.currentTimeMillis()));
                event.setCancelled(true);
            }
        }
    };
}