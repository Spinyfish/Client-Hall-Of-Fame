package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenSpikes extends WorldGenerator {
    private final Block baseBlockRequired;

    public WorldGenSpikes(final Block p_i45464_1_) {
        this.baseBlockRequired = p_i45464_1_;
    }

    public boolean generate(final World worldIn, final Random rand, final BlockPos position) {
        if (worldIn.isAirBlock(position) && worldIn.getBlockState(position.down()).getBlock() == this.baseBlockRequired) {
            final int i = rand.nextInt(32) + 6;
            final int j = rand.nextInt(4) + 1;
            final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for (int k = position.getX() - j; k <= position.getX() + j; ++k) {
                for (int l = position.getZ() - j; l <= position.getZ() + j; ++l) {
                    final int i1 = k - position.getX();
                    final int j1 = l - position.getZ();

                    if (i1 * i1 + j1 * j1 <= j * j + 1 && worldIn.getBlockState(blockpos$mutableblockpos.func_181079_c(k, position.getY() - 1, l)).getBlock() != this.baseBlockRequired) {
                        return false;
                    }
                }
            }

            for (int l1 = position.getY(); l1 < position.getY() + i && l1 < 256; ++l1) {
                for (int i2 = position.getX() - j; i2 <= position.getX() + j; ++i2) {
                    for (int j2 = position.getZ() - j; j2 <= position.getZ() + j; ++j2) {
                        final int k2 = i2 - position.getX();
                        final int k1 = j2 - position.getZ();

                        if (k2 * k2 + k1 * k1 <= j * j + 1) {
                            worldIn.setBlockState(new BlockPos(i2, l1, j2), Blocks.obsidian.getDefaultState(), 2);
                        }
                    }
                }
            }

            final Entity entity = new EntityEnderCrystal(worldIn);
            entity.setLocationAndAngles((float) position.getX() + 0.5F, position.getY() + i, (float) position.getZ() + 0.5F, rand.nextFloat() * 360.0F, 0.0F);
            worldIn.spawnEntityInWorld(entity);
            worldIn.setBlockState(position.up(i), Blocks.bedrock.getDefaultState(), 2);
            return true;
        } else {
            return false;
        }
    }
}
