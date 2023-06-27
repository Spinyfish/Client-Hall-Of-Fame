package net.minecraft.item;

import net.minecraft.creativetab.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.block.material.*;
import net.minecraft.init.*;

public class ItemFireball extends Item
{
    public ItemFireball() {
        this.setCreativeTab(CreativeTabs.tabMisc);
    }
    
    @Override
    public boolean onItemUse(final ItemStack stack, final EntityPlayer playerIn, final World worldIn, BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
        pos = pos.offset(side);
        if (!playerIn.canPlayerEdit(pos, side, stack)) {
            return false;
        }
        if (worldIn.getBlockState(pos).getBlock().getMaterial() == Material.air) {
            worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, "item.fireCharge.use", 1.0f, (ItemFireball.itemRand.nextFloat() - ItemFireball.itemRand.nextFloat()) * 0.2f + 1.0f);
            worldIn.setBlockState(pos, Blocks.fire.getDefaultState());
        }
        if (!playerIn.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        return true;
    }
}
