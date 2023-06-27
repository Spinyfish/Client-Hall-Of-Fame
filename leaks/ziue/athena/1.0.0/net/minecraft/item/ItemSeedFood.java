package net.minecraft.item;

import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraft.util.*;

public class ItemSeedFood extends ItemFood
{
    private Block crops;
    private Block soilId;
    
    public ItemSeedFood(final int healAmount, final float saturation, final Block crops, final Block soil) {
        super(healAmount, saturation, false);
        this.crops = crops;
        this.soilId = soil;
    }
    
    @Override
    public boolean onItemUse(final ItemStack stack, final EntityPlayer playerIn, final World worldIn, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (side != EnumFacing.UP) {
            return false;
        }
        if (!playerIn.canPlayerEdit(pos.offset(side), side, stack)) {
            return false;
        }
        if (worldIn.getBlockState(pos).getBlock() == this.soilId && worldIn.isAirBlock(pos.up())) {
            worldIn.setBlockState(pos.up(), this.crops.getDefaultState());
            --stack.stackSize;
            return true;
        }
        return false;
    }
}
