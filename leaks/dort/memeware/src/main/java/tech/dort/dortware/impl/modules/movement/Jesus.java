package tech.dort.dortware.impl.modules.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.block.BlockLiquid;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.impl.events.BlockCollisionEvent;
import tech.dort.dortware.impl.events.JumpEvent;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.utils.movement.MotionUtils;

public class Jesus extends Module {

    public Jesus(ModuleData moduleData) {
        super(moduleData);
    }

    @Subscribe
    public void onCollide(BlockCollisionEvent event) {
        if (event.getBlock() instanceof BlockLiquid) {
            if (mc.thePlayer.isSneaking())
                return;
            double x = event.getX();
            double y = event.getY();
            double z = event.getZ();
            if (y < mc.thePlayer.posY) {
                event.setAxisAlignedBB(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 0.9999, 15).offset(x, y, z));
            }
        }
    }

    @Subscribe
    public void onJump(JumpEvent event) {
        event.forceCancel(MotionUtils.isOnWater());
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packetPlayer = event.getPacket();
            if (MotionUtils.isOnWater() && mc.thePlayer.ticksExisted % 2 == 0) {
                packetPlayer.y += 0.01D;
                packetPlayer.onGround = false;
            }
        }
    }
}
