package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPosition;
import net.minecraft.world.World;

public abstract class WorldGenerator
{
    private final boolean doBlockNotify;

    public WorldGenerator()
    {
        this(false);
    }

    public WorldGenerator(boolean notify)
    {
        this.doBlockNotify = notify;
    }

    public abstract boolean generate(World worldIn, Random rand, BlockPosition position);

    public void func_175904_e()
    {
    }

    protected void setBlockAndNotifyAdequately(World worldIn, BlockPosition pos, IBlockState state)
    {
        if (this.doBlockNotify)
        {
            worldIn.setBlockState(pos, state, 3);
        }
        else
        {
            worldIn.setBlockState(pos, state, 2);
        }
    }
}
