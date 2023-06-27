package net.minecraft.block;

import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.block.properties.*;
import net.minecraft.tileentity.*;
import net.minecraft.dispenser.*;
import net.minecraft.inventory.*;

public class BlockDropper extends BlockDispenser
{
    private final IBehaviorDispenseItem dropBehavior;
    
    public BlockDropper() {
        this.dropBehavior = new BehaviorDefaultDispenseItem();
    }
    
    @Override
    protected IBehaviorDispenseItem getBehavior(final ItemStack stack) {
        return this.dropBehavior;
    }
    
    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        return new TileEntityDropper();
    }
    
    @Override
    protected void dispense(final World worldIn, final BlockPos pos) {
        final BlockSourceImpl blocksourceimpl = new BlockSourceImpl(worldIn, pos);
        final TileEntityDispenser tileentitydispenser = blocksourceimpl.getBlockTileEntity();
        if (tileentitydispenser != null) {
            final int i = tileentitydispenser.getDispenseSlot();
            if (i < 0) {
                worldIn.playAuxSFX(1001, pos, 0);
            }
            else {
                final ItemStack itemstack = tileentitydispenser.getStackInSlot(i);
                if (itemstack != null) {
                    final EnumFacing enumfacing = worldIn.getBlockState(pos).getValue((IProperty<EnumFacing>)BlockDropper.FACING);
                    final BlockPos blockpos = pos.offset(enumfacing);
                    final IInventory iinventory = TileEntityHopper.getInventoryAtPosition(worldIn, blockpos.getX(), blockpos.getY(), blockpos.getZ());
                    ItemStack itemstack2;
                    if (iinventory == null) {
                        itemstack2 = this.dropBehavior.dispense(blocksourceimpl, itemstack);
                        if (itemstack2 != null && itemstack2.stackSize <= 0) {
                            itemstack2 = null;
                        }
                    }
                    else {
                        itemstack2 = TileEntityHopper.putStackInInventoryAllSlots(iinventory, itemstack.copy().splitStack(1), enumfacing.getOpposite());
                        if (itemstack2 == null) {
                            final ItemStack copy;
                            itemstack2 = (copy = itemstack.copy());
                            if (--copy.stackSize <= 0) {
                                itemstack2 = null;
                            }
                        }
                        else {
                            itemstack2 = itemstack.copy();
                        }
                    }
                    tileentitydispenser.setInventorySlotContents(i, itemstack2);
                }
            }
        }
    }
}
