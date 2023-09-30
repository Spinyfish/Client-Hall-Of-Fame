/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;

public interface IBlockAccess {
    public TileEntity getTileEntity(BlockPos var1);

    public int getCombinedLight(BlockPos var1, int var2);

    public IBlockState getBlockState(BlockPos var1);

    public boolean isAirBlock(BlockPos var1);

    public BiomeGenBase getBiomeGenForCoords(BlockPos var1);

    public boolean extendedLevelsInChunkCache();

    public int getStrongPower(BlockPos var1, EnumFacing var2);

    public WorldType getWorldType();
}

