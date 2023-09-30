package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPosition;
import net.minecraft.world.World;

import java.util.Random;

public interface IGrowable
{
    /**
     * Whether this IGrowable can grow
     */
    boolean canGrow(World worldIn, BlockPosition pos, IBlockState state, boolean isClient);

    boolean canUseBonemeal(World worldIn, Random rand, BlockPosition pos, IBlockState state);

    void grow(World worldIn, Random rand, BlockPosition pos, IBlockState state);
}
