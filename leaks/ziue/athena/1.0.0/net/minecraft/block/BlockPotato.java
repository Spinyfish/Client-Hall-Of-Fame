package net.minecraft.block;

import net.minecraft.init.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.block.state.*;
import net.minecraft.block.properties.*;
import net.minecraft.item.*;

public class BlockPotato extends BlockCrops
{
    @Override
    protected Item getSeed() {
        return Items.potato;
    }
    
    @Override
    protected Item getCrop() {
        return Items.potato;
    }
    
    @Override
    public void dropBlockAsItemWithChance(final World worldIn, final BlockPos pos, final IBlockState state, final float chance, final int fortune) {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        if (!worldIn.isRemote && state.getValue((IProperty<Integer>)BlockPotato.AGE) >= 7 && worldIn.rand.nextInt(50) == 0) {
            Block.spawnAsEntity(worldIn, pos, new ItemStack(Items.poisonous_potato));
        }
    }
}
