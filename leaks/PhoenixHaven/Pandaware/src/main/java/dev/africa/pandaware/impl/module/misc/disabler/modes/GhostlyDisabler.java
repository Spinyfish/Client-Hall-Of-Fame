package dev.africa.pandaware.impl.module.misc.disabler.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.event.player.UpdateEvent;
import dev.africa.pandaware.impl.module.misc.disabler.DisablerModule;
import dev.africa.pandaware.impl.ui.notification.Notification;
import dev.africa.pandaware.utils.math.TimeHelper;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class GhostlyDisabler extends ModuleMode<DisablerModule> {
    public GhostlyDisabler(String name, DisablerModule parent) {
        super(name, parent);
    }

    private final Queue<Packet<?>> linkedQueue = new LinkedBlockingQueue<>();
    private final TimeHelper timer = new TimeHelper();
    private boolean expectedTeleport;
    private boolean funny;

    @EventHandler
    EventCallback<UpdateEvent> onUpdate = event -> {
        if (mc.thePlayer == null) {
            return;
        }
        if (mc.thePlayer.ticksExisted < 3) {
            this.linkedQueue.clear();
            this.timer.reset();
        }
        if (this.timer.reach(1000L * RandomUtils.nextInt(5, 20))) {
            boolean maxed = this.linkedQueue.size() >= 500;
            if (!maxed && !this.linkedQueue.isEmpty()) {
                Packet<?> packet = this.linkedQueue.poll();
                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(packet);
            }
            this.timer.reset();
        }
    };

    @EventHandler
    private final EventCallback<PacketEvent> packetEventEventCallback = event -> {
        if (event.getPacket() instanceof C00PacketKeepAlive) {
            event.cancel();
            this.linkedQueue.add(event.getPacket());
        }

        if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
            C0FPacketConfirmTransaction packet = event.getPacket();
            if (packet.getUid() > 0 && packet.getUid() < 75) {
                return;
            }
            event.cancel();
            this.linkedQueue.add(event.getPacket());
        }

        if (event.getPacket() instanceof S08PacketPlayerPosLook && this.expectedTeleport) {
            this.expectedTeleport = false;
            S08PacketPlayerPosLook packet = event.getPacket();
            if (mc.thePlayer.ticksExisted > 50) {
                event.cancel();
                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(
                        packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), false
                ));
            }
        }

        if (event.getPacket() instanceof C03PacketPlayer) {
            if(event.getState() != PacketEvent.State.SEND) return;

            C03PacketPlayer packet = event.getPacket();

            if (mc.thePlayer.ticksExisted % 50 == 0) {
                this.expectedTeleport = true;
                if (!funny) {
                    Client.getInstance().getNotificationManager().addNotification(Notification.Type.SUCCESS, "Did funny", 1);
                    funny = true;
                }

                packet.setOnGround(false);
                packet.setY(10);
            }
        }

    };

    @Override
    public void onEnable() {
        super.onEnable();
        funny = false;
        Client.getInstance().getNotificationManager().addNotification(Notification.Type.WARNING, "Wait for the 'Did funny' notification before flying.", 5);
    }
}
