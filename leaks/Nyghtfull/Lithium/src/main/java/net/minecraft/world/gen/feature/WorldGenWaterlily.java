package net.minecraft.world.gen.feature;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPosition;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenWaterlily extends WorldGenerator
{
    public boolean generate(World worldIn, Random rand, BlockPosition position)
    {
        for (int i = 0; i < 10; ++i)
        {
            int j = position.getX() + rand.nextInt(8) - rand.nextInt(8);
            int k = position.getY() + rand.nextInt(4) - rand.nextInt(4);
            int l = position.getZ() + rand.nextInt(8) - rand.nextInt(8);

            if (worldIn.isAirBlock(new BlockPosition(j, k, l)) && Blocks.waterlily.canPlaceBlockAt(worldIn, new BlockPosition(j, k, l)))
            {
                worldIn.setBlockState(new BlockPosition(j, k, l), Blocks.waterlily.getDefaultState(), 2);
            }
        }

        return true;
    }
}
