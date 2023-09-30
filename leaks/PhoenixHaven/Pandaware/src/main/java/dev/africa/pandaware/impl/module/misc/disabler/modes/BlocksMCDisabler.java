package dev.africa.pandaware.impl.module.misc.disabler.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.event.player.UpdateEvent;
import dev.africa.pandaware.impl.module.misc.disabler.DisablerModule;
import dev.africa.pandaware.impl.ui.notification.Notification;
import dev.africa.pandaware.switcher.ViaMCP;
import dev.africa.pandaware.switcher.protocols.ProtocolCollection;
import dev.africa.pandaware.utils.math.TimeHelper;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import dev.africa.pandaware.utils.network.ProtocolUtils;
import lombok.var;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlocksMCDisabler extends ModuleMode<DisablerModule> {
    public BlocksMCDisabler(String name, DisablerModule parent) {
        super(name, parent);
    }

    private final TimeHelper timer = new TimeHelper();
    private final Queue<Packet<?>> linkedQueue = new LinkedBlockingQueue<>();
    private boolean expectedTeleport;
    private boolean sendAll;
    private boolean hasDisabled;

    @Override
    public void onEnable() {
        this.timer.reset();
        this.linkedQueue.clear();
        this.expectedTeleport = false;
        this.sendAll = true;
        this.hasDisabled = false;
        if (ViaMCP.getInstance().getVersion() != ProtocolCollection.R1_9.getVersion().getVersion()) {
            this.getParent().toggle(false);
            Client.getInstance().getNotificationManager().addNotification(Notification.Type.NOTIFY, "Please switch to 1.9", 2);
        }
    }

    @EventHandler
    EventCallback<UpdateEvent> onUpdate = event -> {
        if (mc.thePlayer == null) {
            return;
        }

        if (mc.thePlayer.ticksExisted < 3) {
            this.linkedQueue.clear();
            this.timer.reset();
        }

        if (mc.thePlayer != null && mc.thePlayer.ticksExisted < 150) {
            this.sendAll = true;
        }

        if (this.sendAll && this.timer.reach(1000L * RandomUtils.nextInt(5, 20))) {
            boolean maxed = this.linkedQueue.size() >= 500;

            if (!maxed && !this.linkedQueue.isEmpty()) {
                Packet<?> packet = this.linkedQueue.poll();

                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(packet);
            }

            this.timer.reset();
        }
    };

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (mc.thePlayer == null) {
            return;
        }

        if (event.getPacket() instanceof S08PacketPlayerPosLook && (this.expectedTeleport)) {
            this.expectedTeleport = false;

            S08PacketPlayerPosLook packet = event.getPacket();

            if (mc.thePlayer.ticksExisted > 50) {
                event.cancel();

                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer
                        .C06PacketPlayerPosLook(packet.getX(),
                        packet.getY(),
                        packet.getZ(),
                        packet.getYaw(),
                        packet.getPitch(), true));
            }
        }

        if (event.getPacket() instanceof C00PacketKeepAlive) {
            event.cancel();

            this.linkedQueue.add(event.getPacket());
        }

        if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
            var packet = (C0FPacketConfirmTransaction) event.getPacket();

            if (packet.getUid() > 0 && packet.getUid() < 75) {
                return;
            }

            this.linkedQueue.add(event.getPacket());
            event.cancel();
        }

        if (event.getPacket() instanceof C03PacketPlayer) {
            var packet = (C03PacketPlayer) event.getPacket();

            if (!this.hasDisabled || mc.thePlayer.ticksExisted % 100 == 0) {
                this.hasDisabled = true;

                this.expectedTeleport = true;
                double offset = -(0.015625 / 3);

                packet.setOnGround(false);
                packet.setY(offset);
            }
        }

        if (mc.thePlayer != null && mc.thePlayer.ticksExisted < 8) {
            this.linkedQueue.clear();
            this.timer.reset();
        }
    };

    @Override
    public String getInformationSuffix() {
        return this.linkedQueue.size() + "";
    }
}
