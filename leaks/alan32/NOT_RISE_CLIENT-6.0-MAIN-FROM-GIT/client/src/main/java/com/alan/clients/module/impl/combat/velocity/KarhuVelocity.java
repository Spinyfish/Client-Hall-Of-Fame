package com.alan.clients.module.impl.combat.velocity;

import com.alan.clients.module.impl.combat.Velocity;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.BlockAABBEvent;
import com.alan.clients.value.Mode;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

public final class KarhuVelocity extends Mode<Velocity> {

    public KarhuVelocity(String name, Velocity parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<BlockAABBEvent> onBlockAABB = event -> {
        if (getParent().onSwing.getValue() || getParent().onSprint.getValue() && !mc.thePlayer.isSwingInProgress) return;

        if (event.getBlock() instanceof BlockAir && mc.thePlayer.hurtTime > 0 && mc.thePlayer.ticksSinceVelocity <= 9) {
            final double x = event.getBlockPos().getX(), y = event.getBlockPos().getY(), z = event.getBlockPos().getZ();

            if (y == Math.floor(mc.thePlayer.posY) + 1) {
                event.setBoundingBox(AxisAlignedBB.fromBounds(0, 0, 0, 1, 0, 1).offset(x, y, z));
            }
        }
    };
}
