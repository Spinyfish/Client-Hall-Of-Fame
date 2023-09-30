package com.alan.clients.module.impl.movement.flight;

import com.alan.clients.module.impl.movement.Flight;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.BlockAABBEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.value.Mode;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

public final class AirWalkFlight extends Mode<Flight> {

    public AirWalkFlight(String name, Flight parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<BlockAABBEvent> onBlockAABB = event -> {

        // Sets The Bounding Box To The Players Y Position.
        if (event.getBlock() instanceof BlockAir && !InstanceAccess.mc.thePlayer.isSneaking()) {
            final double x = event.getBlockPos().getX(), y = event.getBlockPos().getY(), z = event.getBlockPos().getZ();

            if (y < InstanceAccess.mc.thePlayer.posY) {
                event.setBoundingBox(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
            }
        }
    };
}
