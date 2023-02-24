package me.kansio.client.modules.impl.movement.flight.verus;

import me.kansio.client.event.impl.BlockCollisionEvent;
import me.kansio.client.event.impl.MoveEvent;
import me.kansio.client.modules.impl.movement.flight.FlightMode;
import me.kansio.client.utils.player.PlayerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

public class VerusGlide extends FlightMode {
    public VerusGlide() {
        super("Verus Glide");
    }

    @Override
    public void onMove(MoveEvent event) {
        if (mc.thePlayer.ticksExisted % 4 == 0) {
            mc.thePlayer.motionY = 0.0f;
            PlayerUtil.setMotion(0.4f);
        } else {
            PlayerUtil.setMotion(0.1f);
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
                if (mc.thePlayer.ticksExisted % 5 == 0) {
                    event.setAxisAlignedBB(AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1.0F, 5).offset(x, y, z));
                }
            }
        }
    }
}
