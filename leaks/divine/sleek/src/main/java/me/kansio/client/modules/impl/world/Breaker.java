package me.kansio.client.modules.impl.world;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.modules.impl.combat.KillAura;
import me.kansio.client.utils.network.PacketUtil;
import me.kansio.client.utils.rotations.AimUtil;
import me.kansio.client.utils.rotations.Rotation;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleData(
        name = "Breaker",
        category = ModuleCategory.WORLD,
        description = "Breaks beds and cakes"
)
public class Breaker extends Module {

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (KillAura.target != null) return;

        if (!event.isPre()) {
            return;
        }

        for (int radius = 7, x = -radius; x < radius; ++x) {
            for (int y = radius; y > -radius; --y) {
                for (int z = -radius; z < radius; ++z) {
                    final int xPos = (int) mc.thePlayer.posX + x;
                    final int yPos = (int) mc.thePlayer.posY + y;
                    final int zPos = (int) mc.thePlayer.posZ + z;
                    final BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
                    final Block block = mc.theWorld.getBlockState(blockPos).getBlock();
                    if ((block.getBlockState().getBlock() == Block.getBlockById(92) || block.getBlockState().getBlock() == Blocks.bed)) {
                        if (mc.thePlayer.swingProgress == 0f) {
                            Rotation rot = AimUtil.attemptFacePosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                            event.setRotationYaw(rot.getRotationYaw());
                            event.setRotationPitch(rot.getRotationPitch());
                            mc.thePlayer.swingItem();
                            PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                            PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                        }
                    }
                }
            }
        }
    }
}
