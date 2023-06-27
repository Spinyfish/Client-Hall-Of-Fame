package net.minecraft.item;

import net.minecraft.creativetab.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;
import net.minecraft.init.*;
import net.minecraft.block.*;

public class ItemRedstone extends Item
{
    public ItemRedstone() {
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }
    
    @Override
    public boolean onItemUse(final ItemStack stack, final EntityPlayer playerIn, final World worldIn, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        final boolean flag = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);
        final BlockPos blockpos = flag ? pos : pos.offset(side);
        if (!playerIn.canPlayerEdit(blockpos, side, stack)) {
            return false;
        }
        final Block block = worldIn.getBlockState(blockpos).getBlock();
        if (!worldIn.canBlockBePlaced(block, blockpos, false, side, null, stack)) {
            return false;
        }
        if (Blocks.redstone_wire.canPlaceBlockAt(worldIn, blockpos)) {
            --stack.stackSize;
            worldIn.setBlockState(blockpos, Blocks.redstone_wire.getDefaultState());
            return true;
        }
        return false;
    }
}
