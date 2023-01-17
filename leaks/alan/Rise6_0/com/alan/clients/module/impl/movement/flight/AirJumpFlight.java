package com.alan.clients.module.impl.movement.flight;

import com.alan.clients.module.impl.movement.Flight;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.input.MoveInputEvent;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.other.BlockAABBEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.Mode;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

/**
 * @author Nicklas
 * @since 19.08.2022
 */

public class AirJumpFlight extends Mode<Flight> {
    private double y;

    public AirJumpFlight(String name, Flight parent) {
        super(name, parent);
    }

    @Override
    public void onEnable() {
        y = Math.floor(InstanceAccess.mc.thePlayer.posY);
    }

    @Override
    public void onDisable() {
        MoveUtil.stop();
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        if (InstanceAccess.mc.gameSettings.keyBindJump.isKeyDown() || InstanceAccess.mc.gameSettings.keyBindSneak.isKeyDown()) {
            y = InstanceAccess.mc.thePlayer.posY;
        }

        if (InstanceAccess.mc.thePlayer.onGround) {
            InstanceAccess.mc.thePlayer.jump();
        }
    };


    @EventLink()
    public final Listener<BlockAABBEvent> onBlockAABB = event -> {

        if (event.getBlock() instanceof BlockAir && !InstanceAccess.mc.gameSettings.keyBindSneak.isKeyDown() && (InstanceAccess.mc.thePlayer.posY < y + 1 || InstanceAccess.mc.gameSettings.keyBindJump.isKeyDown())) {
            final double x = event.getBlockPos().getX(), y = event.getBlockPos().getY(), z = event.getBlockPos().getZ();

            if (y < InstanceAccess.mc.thePlayer.posY) {
                event.setBoundingBox(AxisAlignedBB.fromBounds(
                        -15,
                        -1,
                        -15,
                        15,
                        1,
                        15
                ).offset(x, y, z));
            }
        }
    };


    @EventLink()
    public final Listener<MoveInputEvent> onMove = event -> {
        event.setSneak(false);
    };
}