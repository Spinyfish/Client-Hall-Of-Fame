package net.minecraft.item;

import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;
import net.minecraft.util.*;

public class ItemSnow extends ItemBlock
{
    public ItemSnow(final Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    @Override
    public boolean onItemUse(final ItemStack stack, final EntityPlayer playerIn, final World worldIn, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (stack.stackSize == 0) {
            return false;
        }
        if (!playerIn.canPlayerEdit(pos, side, stack)) {
            return false;
        }
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();
        BlockPos blockpos = pos;
        if ((side != EnumFacing.UP || block != this.block) && !block.isReplaceable(worldIn, pos)) {
            blockpos = pos.offset(side);
            iblockstate = worldIn.getBlockState(blockpos);
            block = iblockstate.getBlock();
        }
        if (block == this.block) {
            final int i = iblockstate.getValue((IProperty<Integer>)BlockSnow.LAYERS);
            if (i <= 7) {
                final IBlockState iblockstate2 = iblockstate.withProperty((IProperty<Comparable>)BlockSnow.LAYERS, i + 1);
                final AxisAlignedBB axisalignedbb = this.block.getCollisionBoundingBox(worldIn, blockpos, iblockstate2);
                if (axisalignedbb != null && worldIn.checkNoEntityCollision(axisalignedbb) && worldIn.setBlockState(blockpos, iblockstate2, 2)) {
                    worldIn.playSoundEffect(blockpos.getX() + 0.5f, blockpos.getY() + 0.5f, blockpos.getZ() + 0.5f, this.block.stepSound.getPlaceSound(), (this.block.stepSound.getVolume() + 1.0f) / 2.0f, this.block.stepSound.getFrequency() * 0.8f);
                    --stack.stackSize;
                    return true;
                }
            }
        }
        return super.onItemUse(stack, playerIn, worldIn, blockpos, side, hitX, hitY, hitZ);
    }
    
    @Override
    public int getMetadata(final int damage) {
        return damage;
    }
}
