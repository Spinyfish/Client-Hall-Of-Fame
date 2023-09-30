package net.minecraft.world.gen.feature;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPosition;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenMelon extends WorldGenerator
{
    public boolean generate(World worldIn, Random rand, BlockPosition position)
    {
        for (int i = 0; i < 64; ++i)
        {
            BlockPosition blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (Blocks.melon_block.canPlaceBlockAt(worldIn, blockpos) && worldIn.getBlockState(blockpos.down()).getBlock() == Blocks.grass)
            {
                worldIn.setBlockState(blockpos, Blocks.melon_block.getDefaultState(), 2);
            }
        }

        return true;
    }
}
