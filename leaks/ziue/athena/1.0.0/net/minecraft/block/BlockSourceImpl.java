package net.minecraft.block;

import net.minecraft.dispenser.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.block.state.*;
import net.minecraft.tileentity.*;

public class BlockSourceImpl implements IBlockSource
{
    private final World worldObj;
    private final BlockPos pos;
    
    public BlockSourceImpl(final World worldIn, final BlockPos posIn) {
        this.worldObj = worldIn;
        this.pos = posIn;
    }
    
    @Override
    public World getWorld() {
        return this.worldObj;
    }
    
    @Override
    public double getX() {
        return this.pos.getX() + 0.5;
    }
    
    @Override
    public double getY() {
        return this.pos.getY() + 0.5;
    }
    
    @Override
    public double getZ() {
        return this.pos.getZ() + 0.5;
    }
    
    @Override
    public BlockPos getBlockPos() {
        return this.pos;
    }
    
    @Override
    public int getBlockMetadata() {
        final IBlockState iblockstate = this.worldObj.getBlockState(this.pos);
        return iblockstate.getBlock().getMetaFromState(iblockstate);
    }
    
    @Override
    public <T extends TileEntity> T getBlockTileEntity() {
        return (T)this.worldObj.getTileEntity(this.pos);
    }
}
