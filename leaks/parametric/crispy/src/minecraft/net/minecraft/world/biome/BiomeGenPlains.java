package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BiomeGenPlains extends BiomeGenBase
{
    protected boolean field_150628_aC;

    protected BiomeGenPlains(int p_i1986_1_)
    {
        super(p_i1986_1_);
        this.setTemperatureRainfall(0.8F, 0.4F);
        this.setHeight(height_LowPlains);
        this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityHorse.class, 5, 2, 6));
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.flowersPerChunk = 4;
        this.theBiomeDecorator.grassPerChunk = 10;
    }

    public BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos)
    {
        double var3 = GRASS_COLOR_NOISE.func_151601_a((double)pos.getX() / 200.0D, (double)pos.getZ() / 200.0D);
        int var5;

        if (var3 < -0.8D)
        {
            var5 = rand.nextInt(4);

            switch (var5)
            {
                case 0:
                    return BlockFlower.EnumFlowerType.ORANGE_TULIP;

                case 1:
                    return BlockFlower.EnumFlowerType.RED_TULIP;

                case 2:
                    return BlockFlower.EnumFlowerType.PINK_TULIP;

                case 3:
                default:
                    return BlockFlower.EnumFlowerType.WHITE_TULIP;
            }
        }
        else if (rand.nextInt(3) > 0)
        {
            var5 = rand.nextInt(3);
            return var5 == 0 ? BlockFlower.EnumFlowerType.POPPY : (var5 == 1 ? BlockFlower.EnumFlowerType.HOUSTONIA : BlockFlower.EnumFlowerType.OXEYE_DAISY);
        }
        else
        {
            return BlockFlower.EnumFlowerType.DANDELION;
        }
    }

    public void decorate(World worldIn, Random rand, BlockPos pos)
    {
        double var4 = GRASS_COLOR_NOISE.func_151601_a((double)(pos.getX() + 8) / 200.0D, (double)(pos.getZ() + 8) / 200.0D);
        int var6;
        int var7;
        int var8;
        int var9;

        if (var4 < -0.8D)
        {
            this.theBiomeDecorator.flowersPerChunk = 15;
            this.theBiomeDecorator.grassPerChunk = 5;
        }
        else
        {
            this.theBiomeDecorator.flowersPerChunk = 4;
            this.theBiomeDecorator.grassPerChunk = 10;
            DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.GRASS);

            for (var6 = 0; var6 < 7; ++var6)
            {
                var7 = rand.nextInt(16) + 8;
                var8 = rand.nextInt(16) + 8;
                var9 = rand.nextInt(worldIn.getHeight(pos.add(var7, 0, var8)).getY() + 32);
                DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(var7, var9, var8));
            }
        }

        if (this.field_150628_aC)
        {
            DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.SUNFLOWER);

            for (var6 = 0; var6 < 10; ++var6)
            {
                var7 = rand.nextInt(16) + 8;
                var8 = rand.nextInt(16) + 8;
                var9 = rand.nextInt(worldIn.getHeight(pos.add(var7, 0, var8)).getY() + 32);
                DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(var7, var9, var8));
            }
        }

        super.decorate(worldIn, rand, pos);
    }

    protected BiomeGenBase createMutatedBiome(int p_180277_1_)
    {
        BiomeGenPlains var2 = new BiomeGenPlains(p_180277_1_);
        var2.setBiomeName("Sunflower Plains");
        var2.field_150628_aC = true;
        var2.setColor(9286496);
        var2.field_150609_ah = 14273354;
        return var2;
    }
}
