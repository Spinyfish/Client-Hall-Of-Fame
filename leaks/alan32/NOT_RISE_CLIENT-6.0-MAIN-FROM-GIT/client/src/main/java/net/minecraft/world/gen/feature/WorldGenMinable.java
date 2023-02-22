package net.minecraft.world.gen.feature;

import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenMinable extends WorldGenerator {
    private final IBlockState oreBlock;

    /**
     * The number of blocks to generate.
     */
    private final int numberOfBlocks;
    private final Predicate<IBlockState> predicate;

    public WorldGenMinable(final IBlockState state, final int blockCount) {
        this(state, blockCount, BlockHelper.forBlock(Blocks.stone));
    }

    public WorldGenMinable(final IBlockState state, final int blockCount, final Predicate<IBlockState> p_i45631_3_) {
        this.oreBlock = state;
        this.numberOfBlocks = blockCount;
        this.predicate = p_i45631_3_;
    }

    public boolean generate(final World worldIn, final Random rand, final BlockPos position) {
        final float f = rand.nextFloat() * (float) Math.PI;
        final double d0 = (float) (position.getX() + 8) + MathHelper.sin(f) * (float) this.numberOfBlocks / 8.0F;
        final double d1 = (float) (position.getX() + 8) - MathHelper.sin(f) * (float) this.numberOfBlocks / 8.0F;
        final double d2 = (float) (position.getZ() + 8) + MathHelper.cos(f) * (float) this.numberOfBlocks / 8.0F;
        final double d3 = (float) (position.getZ() + 8) - MathHelper.cos(f) * (float) this.numberOfBlocks / 8.0F;
        final double d4 = position.getY() + rand.nextInt(3) - 2;
        final double d5 = position.getY() + rand.nextInt(3) - 2;

        for (int i = 0; i < this.numberOfBlocks; ++i) {
            final float f1 = (float) i / (float) this.numberOfBlocks;
            final double d6 = d0 + (d1 - d0) * (double) f1;
            final double d7 = d4 + (d5 - d4) * (double) f1;
            final double d8 = d2 + (d3 - d2) * (double) f1;
            final double d9 = rand.nextDouble() * (double) this.numberOfBlocks / 16.0D;
            final double d10 = (double) (MathHelper.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0D;
            final double d11 = (double) (MathHelper.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0D;
            final int j = MathHelper.floor_double(d6 - d10 / 2.0D);
            final int k = MathHelper.floor_double(d7 - d11 / 2.0D);
            final int l = MathHelper.floor_double(d8 - d10 / 2.0D);
            final int i1 = MathHelper.floor_double(d6 + d10 / 2.0D);
            final int j1 = MathHelper.floor_double(d7 + d11 / 2.0D);
            final int k1 = MathHelper.floor_double(d8 + d10 / 2.0D);

            for (int l1 = j; l1 <= i1; ++l1) {
                final double d12 = ((double) l1 + 0.5D - d6) / (d10 / 2.0D);

                if (d12 * d12 < 1.0D) {
                    for (int i2 = k; i2 <= j1; ++i2) {
                        final double d13 = ((double) i2 + 0.5D - d7) / (d11 / 2.0D);

                        if (d12 * d12 + d13 * d13 < 1.0D) {
                            for (int j2 = l; j2 <= k1; ++j2) {
                                final double d14 = ((double) j2 + 0.5D - d8) / (d10 / 2.0D);

                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D) {
                                    final BlockPos blockpos = new BlockPos(l1, i2, j2);

                                    if (this.predicate.apply(worldIn.getBlockState(blockpos))) {
                                        worldIn.setBlockState(blockpos, this.oreBlock, 2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
