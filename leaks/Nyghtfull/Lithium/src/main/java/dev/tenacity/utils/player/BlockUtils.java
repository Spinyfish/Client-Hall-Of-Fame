package dev.tenacity.utils.player;

import dev.tenacity.utils.Utils;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.MathHelper;

public class BlockUtils implements Utils {

    public static boolean isValidBlock(BlockPosition pos) {
        return isValidBlock(mc.theWorld.getBlockState(pos).getBlock(), false);
    }

    public static Block getBlockAtPos(BlockPosition pos) {
        IBlockState blockState = mc.theWorld.getBlockState(pos);
        return blockState.getBlock();
    }

    public static boolean isValidBlock(Block block, boolean placing) {
        if (block instanceof BlockCarpet
                || block instanceof BlockSnow
                || block instanceof BlockContainer
                || block instanceof BlockBasePressurePlate
                || block.getMaterial().isLiquid()) {
            return false;
        }
        if (placing && (block instanceof BlockSlab
                || block instanceof BlockStairs
                || block instanceof BlockLadder
                || block instanceof BlockStainedGlassPane
                || block instanceof BlockWall
                || block instanceof BlockWeb
                || block instanceof BlockCactus
                || block instanceof BlockFalling
                || block == Blocks.glass_pane
                || block == Blocks.iron_bars)) {
            return false;
        }
        return (block.getMaterial().isSolid() || !block.isTranslucent() || block.isFullBlock());
    }

    public static boolean isInLiquid() {
        if (mc.thePlayer == null) return false;
        for (int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
                BlockPosition pos = new BlockPosition(x, (int) mc.thePlayer.getEntityBoundingBox().minY, z);
                Block block = mc.theWorld.getBlockState(pos).getBlock();
                if (block != null && !(block instanceof BlockAir)) {
                    return block instanceof BlockLiquid;
                }
            }
        }
        return false;
    }

    public static boolean isOnLiquid() {
        if (mc.thePlayer == null) return false;
        AxisAlignedBB boundingBox = mc.thePlayer.getEntityBoundingBox();
        if (boundingBox != null) {
            boundingBox = boundingBox.contract(0.01D, 0.0D, 0.01D).offset(0.0D, -0.01D, 0.0D);
            boolean onLiquid = false;
            int y = (int) boundingBox.minY;

            for (int x = MathHelper.floor_double(boundingBox.minX); x < MathHelper.floor_double(boundingBox.maxX + 1.0D); ++x) {
                for (int z = MathHelper.floor_double(boundingBox.minZ); z < MathHelper.floor_double(boundingBox.maxZ + 1.0D); ++z) {
                    Block block = mc.theWorld.getBlockState(new BlockPosition(x, y, z)).getBlock();
                    if (block != Blocks.air) {
                        if (!(block instanceof BlockLiquid)) return false;
                        onLiquid = true;
                    }
                }
            }

            return onLiquid;
        }
        return false;
    }

}
