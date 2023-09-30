package me.jinthium.straight.impl.components;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.component.Component;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.movement.WorldEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.event.network.ServerJoinEvent;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PingSpoofComponent extends Component {
    public static final ConcurrentLinkedQueue<PacketUtil.TimedPacket> incomingPackets = new ConcurrentLinkedQueue<>();
    public static final ConcurrentLinkedQueue<PacketUtil.TimedPacket> outgoingPackets = new ConcurrentLinkedQueue<>();
    private static final TimerUtil stopWatch = new TimerUtil();
    public static boolean spoofing;
    public static int delay;
    public static boolean normal, teleport, velocity, world, entity, client = true;

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event ->  {
        if(!event.isPre())
            return;

        for (final PacketUtil.TimedPacket packet : PingSpoofComponent.incomingPackets) {
            if (System.currentTimeMillis() > packet.time() + (PingSpoofComponent.spoofing ? PingSpoofComponent.delay : 0)) {
                try {
                    PacketUtil.receivePacketNoEvent(packet.packet());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                PingSpoofComponent.incomingPackets.remove(packet);
            }
        }

        for (final PacketUtil.TimedPacket packet : PingSpoofComponent.outgoingPackets) {
            if (System.currentTimeMillis() > packet.time() + (PingSpoofComponent.spoofing ? PingSpoofComponent.delay : 0)) {
                try {
                    PacketUtil.sendPacketNoEvent(packet.packet());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                PingSpoofComponent.outgoingPackets.remove(packet);
            }
        }

        if (stopWatch.hasTimeElapsed(60) || mc.thePlayer.ticksExisted <= 20 || !mc.getNetHandler().doneLoadingTerrain) {
            PingSpoofComponent.spoofing = false;

            for (final PacketUtil.TimedPacket packet : PingSpoofComponent.incomingPackets) {
                PacketUtil.receivePacketNoEvent(packet.packet());
                PingSpoofComponent.incomingPackets.remove(packet);
            }

            for (final PacketUtil.TimedPacket packet : PingSpoofComponent.outgoingPackets) {
                PacketUtil.sendPacketNoEvent(packet.packet());
                PingSpoofComponent.outgoingPackets.remove(packet);
            }
        }
    };

    @Callback
    final EventCallback<ServerJoinEvent> serverJoinEventEventCallback = event -> {
        incomingPackets.clear();
        stopWatch.reset();
        PingSpoofComponent.spoofing = false;
    };

    @Callback
    final EventCallback<WorldEvent> worldEventEventCallback = event -> {
        incomingPackets.clear();
        stopWatch.reset();
        PingSpoofComponent.spoofing = false;
    };

    public static void dispatch() {
        for (final PacketUtil.TimedPacket packet : PingSpoofComponent.incomingPackets) {
            try {
                PacketUtil.receivePacketNoEvent(packet.packet());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            PingSpoofComponent.incomingPackets.remove(packet);
        }

        for (final PacketUtil.TimedPacket packet : PingSpoofComponent.outgoingPackets) {
            PacketUtil.sendPacketNoEvent(packet.packet());
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

    @Callback
    final EventCallback<PacketEvent> packetEventCallback = event -> {
        switch(event.getPacketState()){
            case SENDING -> {
                if (!PingSpoofComponent.client || !PingSpoofComponent.spoofing)
                    return;

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
            }
            case RECEIVING -> {
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
            }
        }
    };
}
