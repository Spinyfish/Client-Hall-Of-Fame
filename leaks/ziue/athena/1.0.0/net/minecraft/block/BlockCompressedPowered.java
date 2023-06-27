package net.minecraft.block;

import net.minecraft.block.material.*;
import net.minecraft.world.*;
import net.minecraft.block.state.*;
import net.minecraft.util.*;

public class BlockCompressedPowered extends Block
{
    public BlockCompressedPowered(final Material p_i46386_1_, final MapColor p_i46386_2_) {
        super(p_i46386_1_, p_i46386_2_);
    }
    
    @Override
    public boolean canProvidePower() {
        return true;
    }
    
    @Override
    public int getWeakPower(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state, final EnumFacing side) {
        return 15;
    }
}
