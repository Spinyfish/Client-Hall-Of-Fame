package net.minecraft.world.biome;

import net.minecraft.entity.passive.*;
import net.minecraft.entity.*;
import java.util.*;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.world.chunk.*;
import net.minecraft.init.*;
import net.minecraft.block.*;
import net.minecraft.block.properties.*;

public class BiomeGenTaiga extends BiomeGenBase
{
    private static final WorldGenTaiga1 field_150639_aC;
    private static final WorldGenTaiga2 field_150640_aD;
    private static final WorldGenMegaPineTree field_150641_aE;
    private static final WorldGenMegaPineTree field_150642_aF;
    private static final WorldGenBlockBlob field_150643_aG;
    private int field_150644_aH;
    
    public BiomeGenTaiga(final int p_i45385_1_, final int p_i45385_2_) {
        super(p_i45385_1_);
        this.field_150644_aH = p_i45385_2_;
        this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 8, 4, 4));
        this.theBiomeDecorator.treesPerChunk = 10;
        if (p_i45385_2_ != 1 && p_i45385_2_ != 2) {
            this.theBiomeDecorator.grassPerChunk = 1;
            this.theBiomeDecorator.mushroomsPerChunk = 1;
        }
        else {
            this.theBiomeDecorator.grassPerChunk = 7;
            this.theBiomeDecorator.deadBushPerChunk = 1;
            this.theBiomeDecorator.mushroomsPerChunk = 3;
        }
    }
    
    @Override
    public WorldGenAbstractTree genBigTreeChance(final Random rand) {
        return ((this.field_150644_aH == 1 || this.field_150644_aH == 2) && rand.nextInt(3) == 0) ? ((this.field_150644_aH != 2 && rand.nextInt(13) != 0) ? BiomeGenTaiga.field_150641_aE : BiomeGenTaiga.field_150642_aF) : ((rand.nextInt(3) == 0) ? BiomeGenTaiga.field_150639_aC : BiomeGenTaiga.field_150640_aD);
    }
    
    @Override
    public WorldGenerator getRandomWorldGenForGrass(final Random rand) {
        return (rand.nextInt(5) > 0) ? new WorldGenTallGrass(BlockTallGrass.EnumType.FERN) : new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
    }
    
    @Override
    public void decorate(final World worldIn, final Random rand, final BlockPos pos) {
        if (this.field_150644_aH == 1 || this.field_150644_aH == 2) {
            for (int i = rand.nextInt(3), j = 0; j < i; ++j) {
                final int k = rand.nextInt(16) + 8;
                final int l = rand.nextInt(16) + 8;
                final BlockPos blockpos = worldIn.getHeight(pos.add(k, 0, l));
                BiomeGenTaiga.field_150643_aG.generate(worldIn, rand, blockpos);
            }
        }
        BiomeGenTaiga.DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.FERN);
        for (int i2 = 0; i2 < 7; ++i2) {
            final int j2 = rand.nextInt(16) + 8;
            final int k2 = rand.nextInt(16) + 8;
            final int l2 = rand.nextInt(worldIn.getHeight(pos.add(j2, 0, k2)).getY() + 32);
            BiomeGenTaiga.DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(j2, l2, k2));
        }
        super.decorate(worldIn, rand, pos);
    }
    
    @Override
    public void genTerrainBlocks(final World worldIn, final Random rand, final ChunkPrimer chunkPrimerIn, final int p_180622_4_, final int p_180622_5_, final double p_180622_6_) {
        if (this.field_150644_aH == 1 || this.field_150644_aH == 2) {
            this.topBlock = Blocks.grass.getDefaultState();
            this.fillerBlock = Blocks.dirt.getDefaultState();
            if (p_180622_6_ > 1.75) {
                this.topBlock = Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT);
            }
            else if (p_180622_6_ > -0.95) {
                this.topBlock = Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.PODZOL);
            }
        }
        this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, p_180622_4_, p_180622_5_, p_180622_6_);
    }
    
    @Override
    protected BiomeGenBase createMutatedBiome(final int p_180277_1_) {
        return (this.biomeID == BiomeGenBase.megaTaiga.biomeID) ? new BiomeGenTaiga(p_180277_1_, 2).func_150557_a(5858897, true).setBiomeName("Mega Spruce Taiga").setFillerBlockMetadata(5159473).setTemperatureRainfall(0.25f, 0.8f).setHeight(new Height(this.minHeight, this.maxHeight)) : super.createMutatedBiome(p_180277_1_);
    }
    
    static {
        field_150639_aC = new WorldGenTaiga1();
        field_150640_aD = new WorldGenTaiga2(false);
        field_150641_aE = new WorldGenMegaPineTree(false, false);
        field_150642_aF = new WorldGenMegaPineTree(false, true);
        field_150643_aG = new WorldGenBlockBlob(Blocks.mossy_cobblestone, 0);
    }
}
