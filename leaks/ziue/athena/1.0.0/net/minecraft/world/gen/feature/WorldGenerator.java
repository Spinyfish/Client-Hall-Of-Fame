package net.minecraft.world.gen.feature;

import net.minecraft.world.*;
import java.util.*;
import net.minecraft.util.*;
import net.minecraft.block.state.*;

public abstract class WorldGenerator
{
    private final boolean doBlockNotify;
    
    public WorldGenerator() {
        this(false);
    }
    
    public WorldGenerator(final boolean notify) {
        this.doBlockNotify = notify;
    }
    
    public abstract boolean generate(final World p0, final Random p1, final BlockPos p2);
    
    public void func_175904_e() {
    }
    
    protected void setBlockAndNotifyAdequately(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (this.doBlockNotify) {
            worldIn.setBlockState(pos, state, 3);
        }
        else {
            worldIn.setBlockState(pos, state, 2);
        }
    }
}
