package net.minecraft.block;

import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.block.state.*;
import java.util.*;

public interface IGrowable
{
    boolean canGrow(final World p0, final BlockPos p1, final IBlockState p2, final boolean p3);
    
    boolean canUseBonemeal(final World p0, final Random p1, final BlockPos p2, final IBlockState p3);
    
    void grow(final World p0, final Random p1, final BlockPos p2, final IBlockState p3);
}
