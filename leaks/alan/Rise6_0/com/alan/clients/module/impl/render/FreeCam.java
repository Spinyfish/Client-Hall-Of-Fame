package com.alan.clients.module.impl.render;


import com.alan.clients.component.impl.player.BlinkComponent;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.player.Blink;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.input.MoveInputEvent;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.newevent.impl.packet.PacketSendEvent;
import com.alan.clients.newevent.impl.render.Render3DEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.util.vector.Vector3d;
import com.alan.clients.value.impl.ColorValue;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.impl.NumberValue;
import com.alan.clients.value.impl.SubMode;
import lombok.Getter;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;

import java.awt.*;

@Getter
@ModuleInfo(name = "FreeCam", description = "module.render.freecam.description", category = Category.RENDER)
public final class FreeCam extends Module {
    
    private final NumberValue speed = new NumberValue("Speed", this, 1, 0.1, 9.5, 0.1);
    private Vector3d position, delta;
    private Vector2f rotation;
    private boolean sprinting;

    @Override
    protected void onEnable() {
        position = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        delta = new Vector3d(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
        rotation = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        sprinting = mc.gameSettings.keyBindSprint.isKeyDown();
    }

    @Override
    protected void onDisable() {
        mc.thePlayer.setPosition(position.getX(), position.getY(), position.getZ());
        mc.thePlayer.rotationYaw = rotation.getX();
        mc.thePlayer.rotationPitch = rotation.getY();
        mc.thePlayer.motionX = delta.getX();
        mc.thePlayer.motionY = delta.getY();
        mc.thePlayer.motionZ = delta.getZ();
        mc.gameSettings.keyBindSprint.setPressed(sprinting);
    }

    @EventLink
    public final Listener<PacketSendEvent> send = event -> {
        Packet<?> packet = event.getPacket();

        if (packet instanceof C0APacketAnimation || packet instanceof C03PacketPlayer ||
                packet instanceof C02PacketUseEntity || packet instanceof C0BPacketEntityAction ||
                packet instanceof C08PacketPlayerBlockPlacement) {
            event.setCancelled();
        }
    };

    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {

        final float speed = this.speed.getValue().floatValue();

        event.setSpeed(speed);
    };

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        final float speed = this.speed.getValue().floatValue();

        mc.thePlayer.motionY = 0.0D
                + (mc.gameSettings.keyBindJump.isKeyDown() ? speed : 0.0D)
                - (mc.gameSettings.keyBindSneak.isKeyDown() ? speed : 0.0D);
    };


    @EventLink()
    public final Listener<MoveInputEvent> onMovementInput = event -> {
        event.setSneak(false);
    };
}