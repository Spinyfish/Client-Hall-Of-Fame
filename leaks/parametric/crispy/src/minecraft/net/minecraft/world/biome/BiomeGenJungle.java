package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMelon;
import net.minecraft.world.gen.feature.WorldGenShrub;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenVines;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenJungle extends BiomeGenBase
{
    private boolean field_150614_aC;

    public BiomeGenJungle(int p_i45379_1_, boolean p_i45379_2_)
    {
        super(p_i45379_1_);
        this.field_150614_aC = p_i45379_2_;

        if (p_i45379_2_)
        {
            this.theBiomeDecorator.treesPerChunk = 2;
        }
        else
        {
            this.theBiomeDecorator.treesPerChunk = 50;
        }

        this.theBiomeDecorator.grassPerChunk = 25;
        this.theBiomeDecorator.flowersPerChunk = 4;

        if (!p_i45379_2_)
        {
            this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityOcelot.class, 2, 1, 1));
        }

        this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityChicken.class, 10, 4, 4));
    }

    public WorldGenAbstractTree genBigTreeChance(Random rand)
    {
        return (WorldGenAbstractTree)(rand.nextInt(10) == 0 ? this.worldGeneratorBigTree : (rand.nextInt(2) == 0 ? new WorldGenShrub(BlockPlanks.EnumType.JUNGLE.getMetadata(), BlockPlanks.EnumType.OAK.getMetadata()) : (!this.field_150614_aC && rand.nextInt(3) == 0 ? new WorldGenMegaJungle(false, 10, 20, BlockPlanks.EnumType.JUNGLE.getMetadata(), BlockPlanks.EnumType.JUNGLE.getMetadata()) : new WorldGenTrees(false, 4 + rand.nextInt(7), BlockPlanks.EnumType.JUNGLE.getMetadata(), BlockPlanks.EnumType.JUNGLE.getMetadata(), true))));
    }

    /**
     * Gets a WorldGen appropriate for this biome.
     */
    public WorldGenerator getRandomWorldGenForGrass(Random rand)
    {
        return rand.nextInt(4) == 0 ? new WorldGenTallGrass(BlockTallGrass.EnumType.FERN) : new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
    }

    public void decorate(World worldIn, Random rand, BlockPos pos)
    {
        super.decorate(worldIn, rand, pos);
        int var4 = rand.nextInt(16) + 8;
        int var5 = rand.nextInt(16) + 8;
        int var6 = rand.nextInt(worldIn.getHeight(pos.add(var4, 0, var5)).getY() * 2);
        (new WorldGenMelon()).generate(worldIn, rand, pos.add(var4, var6, var5));
        WorldGenVines var9 = new WorldGenVines();

        for (var5 = 0; var5 < 50; ++var5)
        {
            var6 = rand.nextInt(16) + 8;
            boolean var7 = true;
            int var8 = rand.nextInt(16) + 8;
            var9.generate(worldIn, rand, pos.add(var6, 128, var8));
        }
    }
}
