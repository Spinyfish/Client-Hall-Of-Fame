package me.kansio.client.modules.impl.movement.flight.misc;

import me.kansio.client.event.impl.BlockCollisionEvent;
import me.kansio.client.modules.impl.movement.flight.FlightMode;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

public class Collide extends FlightMode {

    public Collide() {
        super("Collide");
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
