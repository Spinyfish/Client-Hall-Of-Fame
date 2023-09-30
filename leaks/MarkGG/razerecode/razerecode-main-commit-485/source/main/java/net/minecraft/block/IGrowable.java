package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPosition;
import net.minecraft.world.World;

public interface IGrowable
{
    boolean canGrow(World worldIn, BlockPosition pos, IBlockState state, boolean isClient);

    boolean canUseBonemeal(World worldIn, Random rand, BlockPosition pos, IBlockState state);

    void grow(World worldIn, Random rand, BlockPosition pos, IBlockState state);
}
