package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemLead extends Item {
    public ItemLead() {
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    /**
     * Called when a Block is right-clicked with this Item
     *
     * @param pos  The block being right-clicked
     * @param side The side being right-clicked
     */
    public boolean onItemUse(final ItemStack stack, final EntityPlayer playerIn, final World worldIn, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        final Block block = worldIn.getBlockState(pos).getBlock();

        if (block instanceof BlockFence) {
            if (worldIn.isRemote) {
                return true;
            } else {
                attachToFence(playerIn, worldIn, pos);
                return true;
            }
        } else {
            return false;
        }
    }

    public static boolean attachToFence(final EntityPlayer player, final World worldIn, final BlockPos fence) {
        EntityLeashKnot entityleashknot = EntityLeashKnot.getKnotForPosition(worldIn, fence);
        boolean flag = false;
        final double d0 = 7.0D;
        final int i = fence.getX();
        final int j = fence.getY();
        final int k = fence.getZ();

        for (final EntityLiving entityliving : worldIn.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB((double) i - d0, (double) j - d0, (double) k - d0, (double) i + d0, (double) j + d0, (double) k + d0))) {
            if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == player) {
                if (entityleashknot == null) {
                    entityleashknot = EntityLeashKnot.createKnot(worldIn, fence);
                }

                entityliving.setLeashedToEntity(entityleashknot, true);
                flag = true;
            }
        }

        return flag;
    }
}
