package net.minecraft.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.biome.BiomeGenBase;

public interface IBlockAccess
{
    TileEntity getTileEntity(BlockPosition pos);

    int getCombinedLight(BlockPosition pos, int lightValue);

    IBlockState getBlockState(BlockPosition pos);

    boolean isAirBlock(BlockPosition pos);

    BiomeGenBase getBiomeGenForCoords(BlockPosition pos);

    boolean extendedLevelsInChunkCache();

    int getStrongPower(BlockPosition pos, EnumFacing direction);

    WorldType getWorldType();
}
