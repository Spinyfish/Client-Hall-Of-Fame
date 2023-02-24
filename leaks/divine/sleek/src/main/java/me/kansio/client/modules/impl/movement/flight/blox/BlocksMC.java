package me.kansio.client.modules.impl.movement.flight.blox;

import me.kansio.client.event.impl.BlockCollisionEvent;
import me.kansio.client.event.impl.MoveEvent;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.impl.movement.flight.FlightMode;
import me.kansio.client.utils.player.PlayerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

public class BlocksMC extends FlightMode {


    public BlocksMC() {
        super("BlocksMC");
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        super.onUpdate(event);
    }

    @Override
    public void onMove(MoveEvent event) {
        if (mc.thePlayer.ticksExisted % 15 == 0) {
            mc.thePlayer.motionY = 0.0;
            event.setMotionY(0.41999998688698);
            PlayerUtil.setMotion(getFlight().getSpeed().getValue().floatValue());
        }
    }

    @Override
    public void onPacket(PacketEvent event) {
        super.onPacket(event);
    }

    @Override
    public void onCollide(BlockCollisionEvent event) {
        if (mc.thePlayer.ticksExisted % 15 != 0) {
            if (event.getBlock() instanceof BlockAir) {
                if (mc.thePlayer.isSneaking())
                    return;
                double x = event.getX();
                double y = event.getY();
                double z = event.getZ();
                event.setAxisAlignedBB(AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1.0F, 5).offset(x, y, z));

            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
