package net.minecraft.world.biome;

import net.minecraft.init.*;
import net.minecraft.world.*;
import java.util.*;
import net.minecraft.util.*;
import net.minecraft.world.gen.feature.*;

public class BiomeGenDesert extends BiomeGenBase
{
    public BiomeGenDesert(final int p_i1977_1_) {
        super(p_i1977_1_);
        this.spawnableCreatureList.clear();
        this.topBlock = Blocks.sand.getDefaultState();
        this.fillerBlock = Blocks.sand.getDefaultState();
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.deadBushPerChunk = 2;
        this.theBiomeDecorator.reedsPerChunk = 50;
        this.theBiomeDecorator.cactiPerChunk = 10;
        this.spawnableCreatureList.clear();
    }
    
    @Override
    public void decorate(final World worldIn, final Random rand, final BlockPos pos) {
        super.decorate(worldIn, rand, pos);
        if (rand.nextInt(1000) == 0) {
            final int i = rand.nextInt(16) + 8;
            final int j = rand.nextInt(16) + 8;
            final BlockPos blockpos = worldIn.getHeight(pos.add(i, 0, j)).up();
            new WorldGenDesertWells().generate(worldIn, rand, blockpos);
        }
    }
}
