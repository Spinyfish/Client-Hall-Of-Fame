package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenShrub extends WorldGenTrees
{
    private int leavesMetadata;
    private int woodMetadata;

    public WorldGenShrub(int p_i2015_1_, int p_i2015_2_)
    {
        super(false);
        this.woodMetadata = p_i2015_1_;
        this.leavesMetadata = p_i2015_2_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        Block var4;

        while (((var4 = worldIn.getBlockState(position).getBlock()).getMaterial() == Material.air || var4.getMaterial() == Material.leaves) && position.getY() > 0)
        {
            position = position.down();
        }

        Block var5 = worldIn.getBlockState(position).getBlock();

        if (var5 == Blocks.dirt || var5 == Blocks.grass)
        {
            position = position.up();
            this.setBlock(worldIn, position, Blocks.log, this.woodMetadata);

            for (int var6 = position.getY(); var6 <= position.getY() + 2; ++var6)
            {
                int var7 = var6 - position.getY();
                int var8 = 2 - var7;

                for (int var9 = position.getX() - var8; var9 <= position.getX() + var8; ++var9)
                {
                    int var10 = var9 - position.getX();

                    for (int var11 = position.getZ() - var8; var11 <= position.getZ() + var8; ++var11)
                    {
                        int var12 = var11 - position.getZ();

                        if (Math.abs(var10) != var8 || Math.abs(var12) != var8 || rand.nextInt(2) != 0)
                        {
                            BlockPos var13 = new BlockPos(var9, var6, var11);

                            if (!worldIn.getBlockState(var13).getBlock().isFullBlock())
                            {
                                this.setBlock(worldIn, var13, Blocks.leaves, this.leavesMetadata);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
