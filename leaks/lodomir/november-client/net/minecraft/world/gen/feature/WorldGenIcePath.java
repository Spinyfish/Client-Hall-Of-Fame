/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenIcePath
extends WorldGenerator {
    private Block block = Blocks.packed_ice;
    private int basePathWidth;

    public WorldGenIcePath(int p_i45454_1_) {
        this.basePathWidth = p_i45454_1_;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        while (worldIn.isAirBlock(position) && position.getY() > 2) {
            position = position.down();
        }
        if (worldIn.getBlockState(position).getBlock() != Blocks.snow) {
            return false;
        }
        int i = rand.nextInt(this.basePathWidth - 2) + 2;
        int j = 1;
        for (int k = position.getX() - i; k <= position.getX() + i; ++k) {
            for (int l = position.getZ() - i; l <= position.getZ() + i; ++l) {
                int j1;
                int i1 = k - position.getX();
                if (i1 * i1 + (j1 = l - position.getZ()) * j1 > i * i) continue;
                for (int k1 = position.getY() - j; k1 <= position.getY() + j; ++k1) {
                    BlockPos blockpos = new BlockPos(k, k1, l);
                    Block block = worldIn.getBlockState(blockpos).getBlock();
                    if (block != Blocks.dirt && block != Blocks.snow && block != Blocks.ice) continue;
                    worldIn.setBlockState(blockpos, this.block.getDefaultState(), 2);
                }
            }
        }
        return true;
    }
}

