package com.alan.clients.component.impl.player;

import com.alan.clients.api.Rise;
import com.alan.clients.component.Component;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.newevent.impl.other.ServerJoinEvent;
import com.alan.clients.newevent.impl.other.WorldChangeEvent;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.newevent.impl.packet.PacketSendEvent;
import com.alan.clients.util.packet.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import util.time.StopWatch;

import java.util.concurrent.ConcurrentLinkedQueue;

@Rise
public final class PingSpoofComponent extends Component {

    public static final ConcurrentLinkedQueue<PacketUtil.TimedPacket> incomingPackets = new ConcurrentLinkedQueue<>();
    public static final ConcurrentLinkedQueue<PacketUtil.TimedPacket> outgoingPackets = new ConcurrentLinkedQueue<>();
    private static final StopWatch stopWatch = new StopWatch();
    public static boolean spoofing;
    public static int delay;
    public static boolean normal, teleport, velocity, world, entity, client = true;

    @EventLink(value = Priorities.VERY_LOW)
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        for (final PacketUtil.TimedPacket packet : PingSpoofComponent.incomingPackets) {
            if (System.currentTimeMillis() > packet.getTime() + (PingSpoofComponent.spoofing ? PingSpoofComponent.delay : 0)) {
                try {
                    PacketUtil.receiveNoEvent(packet.getPacket());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                PingSpoofComponent.incomingPackets.remove(packet);
            }
        }

        for (final PacketUtil.TimedPacket packet : PingSpoofComponent.outgoingPackets) {
            if (System.currentTimeMillis() > packet.getTime() + (PingSpoofComponent.spoofing ? PingSpoofComponent.delay : 0)) {
                try {
                    PacketUtil.sendNoEvent(packet.getPacket());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                PingSpoofComponent.outgoingPackets.remove(packet);
            }
        }

        if (stopWatch.finished(60) || mc.thePlayer.ticksExisted <= 20 || !mc.getNetHandler().doneLoadingTerrain) {
            PingSpoofComponent.spoofing = false;

            for (final PacketUtil.TimedPacket packet : PingSpoofComponent.incomingPackets) {
                PacketUtil.receiveNoEvent(packet.getPacket());
                PingSpoofComponent.incomingPackets.remove(packet);
            }

            for (final PacketUtil.TimedPacket packet : PingSpoofComponent.outgoingPackets) {
                PacketUtil.sendNoEvent(packet.getPacket());
                PingSpoofComponent.outgoingPackets.remove(packet);
            }
        }
    };

    @EventLink(value = Priorities.VERY_LOW)
    public final Listener<ServerJoinEvent> onServerJoin = event -> {
        incomingPackets.clear();
        stopWatch.reset();
        PingSpoofComponent.spoofing = false;
    };

    @EventLink(value = Priorities.VERY_LOW)
    public final Listener<WorldChangeEvent> onWorldChange = event -> {
        incomingPackets.clear();
        stopWatch.reset();
        PingSpoofComponent.spoofing = false;
    };

    public static void dispatch() {
        for (final PacketUtil.TimedPacket packet : PingSpoofComponent.incomingPackets) {
            try {
                PacketUtil.receiveNoEvent(packet.getPacket());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            PingSpoofComponent.incomingPackets.remove(packet);
        }

        for (final PacketUtil.TimedPacket packet : PingSpoofComponent.outgoingPackets) {
            PacketUtil.sendNoEvent(packet.getPacket());
            PingSpoofComponent.outgoingPackets.remove(packet);
        }
    }

    public static void setSpoofing(final int delay, final boolean normal, final boolean teleport,
                                   final boolean velocity, final boolean world, final boolean entity) {
        PingSpoofComponent.spoofing = true;
        PingSpoofComponent.delay = delay;
        PingSpoofComponent.normal = normal;
        PingSpoofComponent.teleport = teleport;
        PingSpoofComponent.velocity = velocity;
        PingSpoofComponent.world = world;
        PingSpoofComponent.entity = entity;
        PingSpoofComponent.client = false;

        stopWatch.reset();
    }

    public static void setSpoofing(final int delay, final boolean normal, final boolean teleport,
                                   final boolean velocity, final boolean world, final boolean entity, final boolean client) {
        PingSpoofComponent.spoofing = true;
        PingSpoofComponent.delay = delay;
        PingSpoofComponent.normal = normal;
        PingSpoofComponent.teleport = teleport;
        PingSpoofComponent.velocity = velocity;
        PingSpoofComponent.world = world;
        PingSpoofComponent.entity = entity;
        PingSpoofComponent.client = client;

        stopWatch.reset();
    }

    @EventLink(value = Priorities.VERY_LOW)
    public final Listener<PacketSendEvent> onPacketSend = event -> {
        if (!PingSpoofComponent.client || !PingSpoofComponent.spoofing) return;

        final Packet<?> packet = event.getPacket();

        if (packet instanceof C03PacketPlayer || packet instanceof C16PacketClientStatus ||
                packet instanceof C0DPacketCloseWindow || packet instanceof C0EPacketClickWindow ||
                packet instanceof C0BPacketEntityAction || packet instanceof C02PacketUseEntity ||
                packet instanceof C0APacketAnimation || packet instanceof C09PacketHeldItemChange ||
                packet instanceof C18PacketSpectate || packet instanceof C19PacketResourcePackStatus ||
                packet instanceof C17PacketCustomPayload || packet instanceof C15PacketClientSettings ||
                packet instanceof C14PacketTabComplete || packet instanceof C07PacketPlayerDigging ||
                packet instanceof C08PacketPlayerBlockPlacement) {
            outgoingPackets.add(new PacketUtil.TimedPacket(packet, System.currentTimeMillis()));
            event.setCancelled(true);
        }
    };

    @EventLink(value = Priorities.VERY_LOW)
    public final Listener<PacketReceiveEvent> onPacketReceiveEvent = event -> {

        final Packet<?> packet = event.getPacket();

        if (PingSpoofComponent.spoofing && mc.getNetHandler().doneLoadingTerrain) {
            if (((packet instanceof S32PacketConfirmTransaction || packet instanceof S00PacketKeepAlive) && normal) ||

                    ((packet instanceof S08PacketPlayerPosLook || packet instanceof S09PacketHeldItemChange) && teleport) ||

                    (((packet instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity)packet).getEntityID() == mc.thePlayer.getEntityId()) ||
                            packet instanceof S27PacketExplosion) && velocity) ||

                    ((packet instanceof S26PacketMapChunkBulk || packet instanceof S21PacketChunkData ||
                            packet instanceof S23PacketBlockChange || packet instanceof S22PacketMultiBlockChange) && world) ||

                    ((packet instanceof S13PacketDestroyEntities || packet instanceof S14PacketEntity ||
                            packet instanceof S18PacketEntityTeleport ||
                            packet instanceof S20PacketEntityProperties || packet instanceof S19PacketEntityHeadLook) && entity)) {

                incomingPackets.add(new PacketUtil.TimedPacket(packet, System.currentTimeMillis()));
                event.setCancelled(true);
            }
        }
    };
}