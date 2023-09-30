/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSnow
extends ItemBlock {
    public ItemSnow(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        IBlockState iblockstate1;
        AxisAlignedBB axisalignedbb;
        int i;
        if (stack.stackSize == 0) {
            return false;
        }
        if (!playerIn.canPlayerEdit(pos, side, stack)) {
            return false;
        }
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();
        BlockPos blockpos = pos;
        if (!(side == EnumFacing.UP && block == this.block || block.isReplaceable(worldIn, pos))) {
            blockpos = pos.offset(side);
            iblockstate = worldIn.getBlockState(blockpos);
            block = iblockstate.getBlock();
        }
        if (block == this.block && (i = iblockstate.getValue(BlockSnow.LAYERS).intValue()) <= 7 && (axisalignedbb = this.block.getCollisionBoundingBox(worldIn, blockpos, iblockstate1 = iblockstate.withProperty(BlockSnow.LAYERS, i + 1))) != null && worldIn.checkNoEntityCollision(axisalignedbb) && worldIn.setBlockState(blockpos, iblockstate1, 2)) {
            worldIn.playSoundEffect((float)blockpos.getX() + 0.5f, (float)blockpos.getY() + 0.5f, (float)blockpos.getZ() + 0.5f, this.block.stepSound.getPlaceSound(), (this.block.stepSound.getVolume() + 1.0f) / 2.0f, this.block.stepSound.getFrequency() * 0.8f);
            --stack.stackSize;
            return true;
        }
        return super.onItemUse(stack, playerIn, worldIn, blockpos, side, hitX, hitY, hitZ);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }
}

