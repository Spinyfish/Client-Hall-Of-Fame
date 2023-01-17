package com.alan.clients.module.impl.movement.flight;

import com.alan.clients.component.impl.player.RotationComponent;
import com.alan.clients.component.impl.player.rotationcomponent.MovementFix;
import com.alan.clients.module.impl.movement.Flight;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PostMotionEvent;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.newevent.impl.other.TeleportEvent;
import com.alan.clients.newevent.impl.packet.PacketSendEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.util.vector.Vector3d;
import com.alan.clients.value.impl.NumberValue;
import com.alan.clients.value.Mode;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author Alan
 * @since 31.03.2022
 */

public class BlockDropFlight extends Mode<Flight> {

    private final NumberValue speed = new NumberValue("Speed", this, 1, 0.1, 9.5, 0.1);
    private Vector3d position;
    private Vector2f rotation;

    public BlockDropFlight(String name, Flight parent) {
        super(name, parent);
    }

    @Override
    public void onDisable() {
        MoveUtil.stop();

        for (int i = 0; i < 1; ++i) {
            PacketUtil.sendNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(InstanceAccess.mc.thePlayer.posX, InstanceAccess.mc.thePlayer.posY, InstanceAccess.mc.thePlayer.posZ, rotation.getX(), rotation.getY(), false));
        }
    }

    @Override
    public void onEnable() {
        if (InstanceAccess.mc == null || InstanceAccess.mc.thePlayer == null) return;
        this.position = new Vector3d(InstanceAccess.mc.thePlayer.posX, InstanceAccess.mc.thePlayer.posY, InstanceAccess.mc.thePlayer.posZ);
        this.rotation = new Vector2f(InstanceAccess.mc.thePlayer.rotationYaw, InstanceAccess.mc.thePlayer.rotationPitch);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        InstanceAccess.mc.thePlayer.motionY = InstanceAccess.mc.gameSettings.keyBindJump.isKeyDown() ? speed.getValue().floatValue() : InstanceAccess.mc.gameSettings.keyBindSneak.isKeyDown() ? -speed.getValue().floatValue() : 0;

        for (int i = 0; i < 3; ++i) {
            PacketUtil.sendNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(position.getX(), position.getY(), position.getZ(), rotation.getX(), rotation.getY(), false));
        }
    };

    @EventLink
    public final Listener<PostMotionEvent> onPostMotion = event -> {
        for (int i = 0; i < 1; ++i) {
            PacketUtil.sendNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(InstanceAccess.mc.thePlayer.posX, InstanceAccess.mc.thePlayer.posY, InstanceAccess.mc.thePlayer.posZ, rotation.getX(), rotation.getY(), false));
        }
    };

    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {

        final float speed = this.speed.getValue().floatValue();

        event.setSpeed(speed);
    };

    @EventLink()
    public final Listener<TeleportEvent> onTeleport = event -> {

        event.setCancelled(true);
        this.position = new Vector3d(event.getPosX(), event.getPosY(), event.getPosZ());
        this.rotation = new Vector2f(event.getYaw(), event.getPitch());
    };

    @EventLink()
    public final Listener<PacketSendEvent> onPacketSend = event -> {

        Packet packet = event.getPacket();

        if (packet instanceof C03PacketPlayer) {
            event.setCancelled(true);
        } else if (packet instanceof C02PacketUseEntity) {
            PacketUtil.sendNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(InstanceAccess.mc.thePlayer.posX, InstanceAccess.mc.thePlayer.posY, InstanceAccess.mc.thePlayer.posZ, InstanceAccess.mc.thePlayer.rotationYaw, InstanceAccess.mc.thePlayer.rotationPitch, false));
        }
    };

    @EventLink()
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        RotationComponent.setRotations(rotation, 10, MovementFix.OFF);
    };
}