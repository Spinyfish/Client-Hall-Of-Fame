/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenGlowStone1
extends WorldGenerator {
    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        if (!worldIn.isAirBlock(position)) {
            return false;
        }
        if (worldIn.getBlockState(position.up()).getBlock() != Blocks.netherrack) {
            return false;
        }
        worldIn.setBlockState(position, Blocks.glowstone.getDefaultState(), 2);
        for (int i = 0; i < 1500; ++i) {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), -rand.nextInt(12), rand.nextInt(8) - rand.nextInt(8));
            if (worldIn.getBlockState(blockpos).getBlock().getMaterial() != Material.air) continue;
            int j = 0;
            for (EnumFacing enumfacing : EnumFacing.values()) {
                if (worldIn.getBlockState(blockpos.offset(enumfacing)).getBlock() == Blocks.glowstone) {
                    ++j;
                }
                if (j > 1) break;
            }
            if (j != true) continue;
            worldIn.setBlockState(blockpos, Blocks.glowstone.getDefaultState(), 2);
        }
        return true;
    }
}

