package net.minecraft.world.biome;

import net.minecraft.block.BlockSilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class BiomeGenHills extends BiomeGenBase {
    private final WorldGenerator theWorldGenerator = new WorldGenMinable(Blocks.monster_egg.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.STONE), 9);
    private final WorldGenTaiga2 field_150634_aD = new WorldGenTaiga2(false);
    private final int field_150635_aE = 0;
    private final int field_150636_aF = 1;
    private final int field_150637_aG = 2;
    private int field_150638_aH;

    protected BiomeGenHills(final int p_i45373_1_, final boolean p_i45373_2_) {
        super(p_i45373_1_);
        this.field_150638_aH = this.field_150635_aE;

        if (p_i45373_2_) {
            this.theBiomeDecorator.treesPerChunk = 3;
            this.field_150638_aH = this.field_150636_aF;
        }
    }

    public WorldGenAbstractTree genBigTreeChance(final Random rand) {
        return rand.nextInt(3) > 0 ? this.field_150634_aD : super.genBigTreeChance(rand);
    }

    public void decorate(final World worldIn, final Random rand, final BlockPos pos) {
        super.decorate(worldIn, rand, pos);
        int i = 3 + rand.nextInt(6);

        for (int j = 0; j < i; ++j) {
            final int k = rand.nextInt(16);
            final int l = rand.nextInt(28) + 4;
            final int i1 = rand.nextInt(16);
            final BlockPos blockpos = pos.add(k, l, i1);

            if (worldIn.getBlockState(blockpos).getBlock() == Blocks.stone) {
                worldIn.setBlockState(blockpos, Blocks.emerald_ore.getDefaultState(), 2);
            }
        }

        for (i = 0; i < 7; ++i) {
            final int j1 = rand.nextInt(16);
            final int k1 = rand.nextInt(64);
            final int l1 = rand.nextInt(16);
            this.theWorldGenerator.generate(worldIn, rand, pos.add(j1, k1, l1));
        }
    }

    public void genTerrainBlocks(final World worldIn, final Random rand, final ChunkPrimer chunkPrimerIn, final int p_180622_4_, final int p_180622_5_, final double p_180622_6_) {
        this.topBlock = Blocks.grass.getDefaultState();
        this.fillerBlock = Blocks.dirt.getDefaultState();

        if ((p_180622_6_ < -1.0D || p_180622_6_ > 2.0D) && this.field_150638_aH == this.field_150637_aG) {
            this.topBlock = Blocks.gravel.getDefaultState();
            this.fillerBlock = Blocks.gravel.getDefaultState();
        } else if (p_180622_6_ > 1.0D && this.field_150638_aH != this.field_150636_aF) {
            this.topBlock = Blocks.stone.getDefaultState();
            this.fillerBlock = Blocks.stone.getDefaultState();
        }

        this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, p_180622_4_, p_180622_5_, p_180622_6_);
    }

    /**
     * this creates a mutation specific to Hills biomes
     */
    private BiomeGenHills mutateHills(final BiomeGenBase p_150633_1_) {
        this.field_150638_aH = this.field_150637_aG;
        this.func_150557_a(p_150633_1_.color, true);
        this.setBiomeName(p_150633_1_.biomeName + " M");
        this.setHeight(new BiomeGenBase.Height(p_150633_1_.minHeight, p_150633_1_.maxHeight));
        this.setTemperatureRainfall(p_150633_1_.temperature, p_150633_1_.rainfall);
        return this;
    }

    protected BiomeGenBase createMutatedBiome(final int p_180277_1_) {
        return (new BiomeGenHills(p_180277_1_, false)).mutateHills(this);
    }
}
