package me.kansio.client.modules.impl.movement.flight.verus;

import me.kansio.client.event.impl.BlockCollisionEvent;
import me.kansio.client.event.impl.MoveEvent;
import me.kansio.client.modules.impl.movement.flight.FlightMode;
import me.kansio.client.utils.player.PlayerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

public class Verus extends FlightMode {

    public Verus() {
        super("Verus");
    }

    @Override
    public void onMove(MoveEvent event) {
        if (!mc.thePlayer.isInLava() && !mc.thePlayer.isInWater() && !mc.thePlayer.isOnLadder() && mc.thePlayer.ridingEntity == null && mc.thePlayer.hurtTime < 1) {

            mc.gameSettings.keyBindJump.pressed = false;
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                mc.thePlayer.motionY = 0.0;
                if (mc.thePlayer.isMoving()) {
                    PlayerUtil.strafe(0.61f);
                }
                event.setMotionY(0.41999998688698);
            }
            PlayerUtil.strafe();
        }

    }

    @Override
    public void onCollide(BlockCollisionEvent event) {
        if (event.getBlock() instanceof BlockAir) {
            if (mc.thePlayer.isSneaking())
                return;
            double x = event.getX();
            double y = event.getY();
            double z = event.getZ();
            if (y < mc.thePlayer.posY) {
                event.setAxisAlignedBB(AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1.0F, 5).offset(x, y, z));
            }
        }
    }

}
