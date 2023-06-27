package net.minecraft.item;

import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import net.minecraft.block.properties.*;
import net.minecraft.init.*;
import net.minecraft.stats.*;
import net.minecraft.util.*;
import net.minecraft.block.state.*;

public class ItemLilyPad extends ItemColored
{
    public ItemLilyPad(final Block block) {
        super(block, false);
    }
    
    @Override
    public ItemStack onItemRightClick(final ItemStack itemStackIn, final World worldIn, final EntityPlayer playerIn) {
        final MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, true);
        if (movingobjectposition == null) {
            return itemStackIn;
        }
        if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            final BlockPos blockpos = movingobjectposition.getBlockPos();
            if (!worldIn.isBlockModifiable(playerIn, blockpos)) {
                return itemStackIn;
            }
            if (!playerIn.canPlayerEdit(blockpos.offset(movingobjectposition.sideHit), movingobjectposition.sideHit, itemStackIn)) {
                return itemStackIn;
            }
            final BlockPos blockpos2 = blockpos.up();
            final IBlockState iblockstate = worldIn.getBlockState(blockpos);
            if (iblockstate.getBlock().getMaterial() == Material.water && iblockstate.getValue((IProperty<Integer>)BlockLiquid.LEVEL) == 0 && worldIn.isAirBlock(blockpos2)) {
                worldIn.setBlockState(blockpos2, Blocks.waterlily.getDefaultState());
                if (!playerIn.capabilities.isCreativeMode) {
                    --itemStackIn.stackSize;
                }
                playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
            }
        }
        return itemStackIn;
    }
    
    @Override
    public int getColorFromItemStack(final ItemStack stack, final int renderPass) {
        return Blocks.waterlily.getRenderColor(Blocks.waterlily.getStateFromMeta(stack.getMetadata()));
    }
}
