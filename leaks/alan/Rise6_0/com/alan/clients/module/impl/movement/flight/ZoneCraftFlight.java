package com.alan.clients.module.impl.movement.flight;

import com.alan.clients.component.impl.player.BlinkComponent;
import com.alan.clients.component.impl.player.SlotComponent;
import com.alan.clients.component.impl.render.SmoothCameraComponent;
import com.alan.clients.module.impl.movement.Flight;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.newevent.impl.other.BlockAABBEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.util.player.SlotUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.value.Mode;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.AxisAlignedBB;

/**
 * @author Strikeless
 * @since 03.07.2022
 */
public class ZoneCraftFlight extends Mode<Flight> {

    private double y;
    private int ticks;

    public ZoneCraftFlight(String name, Flight parent) {
        super(name, parent);
    }

    @Override
    public void onEnable() {
        y = mc.thePlayer.posY;
        BlinkComponent.blinking = true;
        ticks = 0;
        PlayerUtil.fakeDamage();
    }

    @Override
    public void onDisable() {
        BlinkComponent.blinking = false;
    }

    @EventLink()
    public final Listener<BlockAABBEvent> onBlockAABB = event -> {

        // Sets The Bounding Box To The Players Y Position.
        if (event.getBlock() instanceof BlockAir && !mc.gameSettings.keyBindSneak.isKeyDown() && (mc.thePlayer.posY < y + 1 || mc.gameSettings.keyBindJump.isKeyDown())) {
            final double x = event.getBlockPos().getX(), y = event.getBlockPos().getY(), z = event.getBlockPos().getZ();

            if (y < mc.thePlayer.posY) {
                event.setBoundingBox(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
            }
        }
    };

    @EventLink()
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {

        ticks++;

        final int block = SlotUtil.findBlock();

        if (block != -1) {
            SlotComponent.setSlot(block);
        }

        PacketUtil.send(new C08PacketPlayerBlockPlacement(null));

        mc.timer.timerSpeed = 2;

        if (Math.random() > 0.95) {
            BlinkComponent.dispatch();
        }

        if (mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown()) {
            y = mc.thePlayer.posY;
        }

        if (mc.thePlayer.onGround) {
            MoveUtil.strafe();
            mc.thePlayer.jump();
        }
    };

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        SmoothCameraComponent.setY();
    };
}
