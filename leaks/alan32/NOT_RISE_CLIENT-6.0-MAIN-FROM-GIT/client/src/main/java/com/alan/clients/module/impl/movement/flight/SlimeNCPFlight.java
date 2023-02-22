package com.alan.clients.module.impl.movement.flight;

import com.alan.clients.component.impl.player.BlinkComponent;
import com.alan.clients.component.impl.player.RotationComponent;
import com.alan.clients.component.impl.player.SlotComponent;
import com.alan.clients.component.impl.player.rotationcomponent.MovementFix;
import com.alan.clients.module.impl.movement.Flight;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.newevent.impl.other.BlockAABBEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.util.player.SlotUtil;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.value.Mode;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;

/**
 * @author Alan
 * @since 18/11/2021
 */

public class SlimeNCPFlight extends Mode<Flight> {

    private boolean started;

    public SlimeNCPFlight(String name, Flight parent) {
        super(name, parent);
    }

    @Override
    public void onEnable() {
        started = false;
        BlinkComponent.blinking = true;
    }

    @Override
    public void onDisable() {
        BlinkComponent.blinking = false;
    }

    @EventLink()
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {

        if (InstanceAccess.mc.thePlayer.ticksExisted % 10 == 0) {
            BlinkComponent.dispatch();
        }


        if (!started) {
            final int slot = SlotUtil.findBlock(Blocks.slime_block);

            if (slot == -1) {
                return;
            }

            MoveUtil.stop();

            SlotComponent.setSlot(slot, true);

            RotationComponent.setRotations(new Vector2f(InstanceAccess.mc.thePlayer.rotationYaw, 90), 3, MovementFix.OFF);

            if (InstanceAccess.mc.thePlayer.onGround) {
                InstanceAccess.mc.thePlayer.jump();
            }

            if (RotationComponent.rotations.y >= 89 &&
                    InstanceAccess.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK &&
                    InstanceAccess.mc.thePlayer.posY > InstanceAccess.mc.objectMouseOver.getBlockPos().add(0, 2, 0).getY()) {

                InstanceAccess.mc.playerController.onPlayerRightClick(InstanceAccess.mc.thePlayer, InstanceAccess.mc.theWorld, SlotComponent.getItemStack(),
                        InstanceAccess.mc.objectMouseOver.getBlockPos(), InstanceAccess.mc.objectMouseOver.sideHit, InstanceAccess.mc.objectMouseOver.hitVec);

                InstanceAccess.mc.thePlayer.swingItem();

                started = true;
            }
        } else {
            if (InstanceAccess.mc.thePlayer.motionY > 0) {
                InstanceAccess.mc.thePlayer.motionY = MoveUtil.predictedMotion(0);
            }

            InstanceAccess.mc.timer.timerSpeed = 1.4f;
        }
    };

    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {

        if (!started) {
            event.setForward(0);
            event.setStrafe(0);
        }
    };

    @EventLink()
    public final Listener<BlockAABBEvent> onBlockAABB = event -> {

        if (started) {
            // Sets The Bounding Box To The Players Y Position.
            if (!InstanceAccess.mc.gameSettings.keyBindSneak.isKeyDown()) {
                final double x = event.getBlockPos().getX(), y = event.getBlockPos().getY(), z = event.getBlockPos().getZ();

                if (y < InstanceAccess.mc.thePlayer.posY) {
                    event.setBoundingBox(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
                }
            }
        }
    };
}