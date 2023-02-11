package net.minecraft.block.state.pattern;

import Reality.Realii.utils.cheats.player.Helper;
import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class BlockHelper implements Predicate<IBlockState>
{
    private final Block block;

    private BlockHelper(Block blockType)
    {
        this.block = blockType;
    }

    public static BlockHelper forBlock(Block blockType)
    {
        return new BlockHelper(blockType);
    }

    public boolean apply(IBlockState p_apply_1_)
    {
        return p_apply_1_ != null && p_apply_1_.getBlock() == this.block;
    }

    public static Block getBlock(final double x, final double y, final double z) {
        return Helper.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
    }
    
    public static boolean insideBlock() {
        for (int x = MathHelper.floor_double(Helper.mc.thePlayer.boundingBox.minX); x < MathHelper.floor_double(Helper.mc.thePlayer.boundingBox.maxX) + 1; ++x) {
            for (int y = MathHelper.floor_double(Helper.mc.thePlayer.boundingBox.minY); y < MathHelper.floor_double(Helper.mc.thePlayer.boundingBox.maxY) + 1; ++y) {
                for (int z = MathHelper.floor_double(Helper.mc.thePlayer.boundingBox.minZ); z < MathHelper.floor_double(Helper.mc.thePlayer.boundingBox.maxZ) + 1; ++z) {
                    final Block block = Helper.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block != null && !(block instanceof BlockAir)) {
                        AxisAlignedBB boundingBox = block.getCollisionBoundingBox(Helper.mc.theWorld, new BlockPos(x, y, z), Helper.mc.theWorld.getBlockState(new BlockPos(x, y, z)));
                        if (block instanceof BlockHopper) {
                            boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
                        }
                        if (boundingBox != null && Helper.mc.thePlayer.boundingBox.intersectsWith(boundingBox)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean isOnLiquid() {
        boolean onLiquid = false;
        final int y = (int)Helper.mc.thePlayer.getEntityBoundingBox().offset(0.0, -0.01, 0.0).minY;
        for (int x = MathHelper.floor_double(Helper.mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(Helper.mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int z = MathHelper.floor_double(Helper.mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(Helper.mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
                final Block block = getBlock(x, y, z);
                if (block != null && !(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }
    
    public static boolean isInLiquid() {
        boolean inLiquid = false;
        final int y = (int)Helper.mc.thePlayer.getEntityBoundingBox().minY;
        for (int x = MathHelper.floor_double(Helper.mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(Helper.mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int z = MathHelper.floor_double(Helper.mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(Helper.mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
                final Block block = getBlock(x, y, z);
                if (block != null && !(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    inLiquid = true;
                }
            }
        }
        return inLiquid;
    }
}
