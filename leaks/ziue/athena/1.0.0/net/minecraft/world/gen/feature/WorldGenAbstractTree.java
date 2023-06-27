package net.minecraft.world.gen.feature;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.init.*;
import net.minecraft.world.*;
import java.util.*;
import net.minecraft.util.*;

public abstract class WorldGenAbstractTree extends WorldGenerator
{
    public WorldGenAbstractTree(final boolean p_i45448_1_) {
        super(p_i45448_1_);
    }
    
    protected boolean func_150523_a(final Block p_150523_1_) {
        final Material material = p_150523_1_.getMaterial();
        return material == Material.air || material == Material.leaves || p_150523_1_ == Blocks.grass || p_150523_1_ == Blocks.dirt || p_150523_1_ == Blocks.log || p_150523_1_ == Blocks.log2 || p_150523_1_ == Blocks.sapling || p_150523_1_ == Blocks.vine;
    }
    
    public void func_180711_a(final World worldIn, final Random p_180711_2_, final BlockPos p_180711_3_) {
    }
    
    protected void func_175921_a(final World worldIn, final BlockPos p_175921_2_) {
        if (worldIn.getBlockState(p_175921_2_).getBlock() != Blocks.dirt) {
            this.setBlockAndNotifyAdequately(worldIn, p_175921_2_, Blocks.dirt.getDefaultState());
        }
    }
}
