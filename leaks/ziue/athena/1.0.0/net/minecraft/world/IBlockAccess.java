package net.minecraft.world;

import net.minecraft.tileentity.*;
import net.minecraft.block.state.*;
import net.minecraft.world.biome.*;
import net.minecraft.util.*;

public interface IBlockAccess
{
    TileEntity getTileEntity(final BlockPos p0);
    
    int getCombinedLight(final BlockPos p0, final int p1);
    
    IBlockState getBlockState(final BlockPos p0);
    
    boolean isAirBlock(final BlockPos p0);
    
    BiomeGenBase getBiomeGenForCoords(final BlockPos p0);
    
    boolean extendedLevelsInChunkCache();
    
    int getStrongPower(final BlockPos p0, final EnumFacing p1);
    
    WorldType getWorldType();
}
