/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockPotato
extends BlockCrops {
    @Override
    protected Item getSeed() {
        return Items.potato;
    }

    @Override
    protected Item getCrop() {
        return Items.potato;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        if (!worldIn.isRemote && state.getValue(AGE) >= 7 && worldIn.rand.nextInt(50) == 0) {
            BlockPotato.spawnAsEntity(worldIn, pos, new ItemStack(Items.poisonous_potato));
        }
    }
}

