package net.minecraft.world.gen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.*;

import java.util.List;
import java.util.Random;

public class ChunkProviderGenerate implements IChunkProvider {
    /**
     * RNG.
     */
    private final Random rand;
    private final NoiseGeneratorOctaves field_147431_j;
    private final NoiseGeneratorOctaves field_147432_k;
    private final NoiseGeneratorOctaves field_147429_l;
    private final NoiseGeneratorPerlin field_147430_m;

    /**
     * A NoiseGeneratorOctaves used in generating terrain
     */
    public NoiseGeneratorOctaves noiseGen5;

    /**
     * A NoiseGeneratorOctaves used in generating terrain
     */
    public NoiseGeneratorOctaves noiseGen6;
    public NoiseGeneratorOctaves mobSpawnerNoise;

    /**
     * Reference to the World object.
     */
    private final World worldObj;

    /**
     * are map structures going to be generated (e.g. strongholds)
     */
    private final boolean mapFeaturesEnabled;
    private final WorldType field_177475_o;
    private final double[] field_147434_q;
    private final float[] parabolicField;
    private ChunkProviderSettings settings;
    private Block field_177476_s = Blocks.water;
    private double[] stoneNoise = new double[256];
    private final MapGenBase caveGenerator = new MapGenCaves();

    /**
     * Holds Stronghold Generator
     */
    private final MapGenStronghold strongholdGenerator = new MapGenStronghold();

    /**
     * Holds Village Generator
     */
    private final MapGenVillage villageGenerator = new MapGenVillage();

    /**
     * Holds Mineshaft Generator
     */
    private final MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
    private final MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();

    /**
     * Holds ravine generator
     */
    private final MapGenBase ravineGenerator = new MapGenRavine();
    private final StructureOceanMonument oceanMonumentGenerator = new StructureOceanMonument();

    /**
     * The biomes that are used to generate the chunk
     */
    private BiomeGenBase[] biomesForGeneration;
    double[] field_147427_d;
    double[] field_147428_e;
    double[] field_147425_f;
    double[] field_147426_g;

    public ChunkProviderGenerate(final World worldIn, final long p_i45636_2_, final boolean p_i45636_4_, final String p_i45636_5_) {
        this.worldObj = worldIn;
        this.mapFeaturesEnabled = p_i45636_4_;
        this.field_177475_o = worldIn.getWorldInfo().getTerrainType();
        this.rand = new Random(p_i45636_2_);
        this.field_147431_j = new NoiseGeneratorOctaves(this.rand, 16);
        this.field_147432_k = new NoiseGeneratorOctaves(this.rand, 16);
        this.field_147429_l = new NoiseGeneratorOctaves(this.rand, 8);
        this.field_147430_m = new NoiseGeneratorPerlin(this.rand, 4);
        this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
        this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
        this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
        this.field_147434_q = new double[825];
        this.parabolicField = new float[25];

        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                final float f = 10.0F / MathHelper.sqrt_float((float) (i * i + j * j) + 0.2F);
                this.parabolicField[i + 2 + (j + 2) * 5] = f;
            }
        }

        if (p_i45636_5_ != null) {
            this.settings = ChunkProviderSettings.Factory.jsonToFactory(p_i45636_5_).func_177864_b();
            this.field_177476_s = this.settings.useLavaOceans ? Blocks.lava : Blocks.water;
            worldIn.func_181544_b(this.settings.seaLevel);
        }
    }

    public void setBlocksInChunk(final int p_180518_1_, final int p_180518_2_, final ChunkPrimer p_180518_3_) {
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, p_180518_1_ * 4 - 2, p_180518_2_ * 4 - 2, 10, 10);
        this.func_147423_a(p_180518_1_ * 4, 0, p_180518_2_ * 4);

        for (int i = 0; i < 4; ++i) {
            final int j = i * 5;
            final int k = (i + 1) * 5;

            for (int l = 0; l < 4; ++l) {
                final int i1 = (j + l) * 33;
                final int j1 = (j + l + 1) * 33;
                final int k1 = (k + l) * 33;
                final int l1 = (k + l + 1) * 33;

                for (int i2 = 0; i2 < 32; ++i2) {
                    final double d0 = 0.125D;
                    double d1 = this.field_147434_q[i1 + i2];
                    double d2 = this.field_147434_q[j1 + i2];
                    double d3 = this.field_147434_q[k1 + i2];
                    double d4 = this.field_147434_q[l1 + i2];
                    final double d5 = (this.field_147434_q[i1 + i2 + 1] - d1) * d0;
                    final double d6 = (this.field_147434_q[j1 + i2 + 1] - d2) * d0;
                    final double d7 = (this.field_147434_q[k1 + i2 + 1] - d3) * d0;
                    final double d8 = (this.field_147434_q[l1 + i2 + 1] - d4) * d0;

                    for (int j2 = 0; j2 < 8; ++j2) {
                        final double d9 = 0.25D;
                        double d10 = d1;
                        double d11 = d2;
                        final double d12 = (d3 - d1) * d9;
                        final double d13 = (d4 - d2) * d9;

                        for (int k2 = 0; k2 < 4; ++k2) {
                            final double d14 = 0.25D;
                            final double d16 = (d11 - d10) * d14;
                            double lvt_45_1_ = d10 - d16;

                            for (int l2 = 0; l2 < 4; ++l2) {
                                if ((lvt_45_1_ += d16) > 0.0D) {
                                    p_180518_3_.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, Blocks.stone.getDefaultState());
                                } else if (i2 * 8 + j2 < this.settings.seaLevel) {
                                    p_180518_3_.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, this.field_177476_s.getDefaultState());
                                }
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
    }

    public void replaceBlocksForBiome(final int p_180517_1_, final int p_180517_2_, final ChunkPrimer p_180517_3_, final BiomeGenBase[] p_180517_4_) {
        final double d0 = 0.03125D;
        this.stoneNoise = this.field_147430_m.func_151599_a(this.stoneNoise, p_180517_1_ * 16, p_180517_2_ * 16, 16, 16, d0 * 2.0D, d0 * 2.0D, 1.0D);

        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                final BiomeGenBase biomegenbase = p_180517_4_[j + i * 16];
                biomegenbase.genTerrainBlocks(this.worldObj, this.rand, p_180517_3_, p_180517_1_ * 16 + i, p_180517_2_ * 16 + j, this.stoneNoise[j + i * 16]);
            }
        }
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(final int x, final int z) {
        this.rand.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
        final ChunkPrimer chunkprimer = new ChunkPrimer();
        this.setBlocksInChunk(x, z, chunkprimer);
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, x * 16, z * 16, 16, 16);
        this.replaceBlocksForBiome(x, z, chunkprimer, this.biomesForGeneration);

        if (this.settings.useCaves) {
            this.caveGenerator.generate(this, this.worldObj, x, z, chunkprimer);
        }

        if (this.settings.useRavines) {
            this.ravineGenerator.generate(this, this.worldObj, x, z, chunkprimer);
        }

        if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
            this.mineshaftGenerator.generate(this, this.worldObj, x, z, chunkprimer);
        }

        if (this.settings.useVillages && this.mapFeaturesEnabled) {
            this.villageGenerator.generate(this, this.worldObj, x, z, chunkprimer);
        }

        if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
            this.strongholdGenerator.generate(this, this.worldObj, x, z, chunkprimer);
        }

        if (this.settings.useTemples && this.mapFeaturesEnabled) {
            this.scatteredFeatureGenerator.generate(this, this.worldObj, x, z, chunkprimer);
        }

        if (this.settings.useMonuments && this.mapFeaturesEnabled) {
            this.oceanMonumentGenerator.generate(this, this.worldObj, x, z, chunkprimer);
        }

        final Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
        final byte[] abyte = chunk.getBiomeArray();

        for (int i = 0; i < abyte.length; ++i) {
            abyte[i] = (byte) this.biomesForGeneration[i].biomeID;
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    private void func_147423_a(int p_147423_1_, final int p_147423_2_, int p_147423_3_) {
        this.field_147426_g = this.noiseGen6.generateNoiseOctaves(this.field_147426_g, p_147423_1_, p_147423_3_, 5, 5, this.settings.depthNoiseScaleX, this.settings.depthNoiseScaleZ, this.settings.depthNoiseScaleExponent);
        final float f = this.settings.coordinateScale;
        final float f1 = this.settings.heightScale;
        this.field_147427_d = this.field_147429_l.generateNoiseOctaves(this.field_147427_d, p_147423_1_, p_147423_2_, p_147423_3_, 5, 33, 5, f / this.settings.mainNoiseScaleX, f1 / this.settings.mainNoiseScaleY, f / this.settings.mainNoiseScaleZ);
        this.field_147428_e = this.field_147431_j.generateNoiseOctaves(this.field_147428_e, p_147423_1_, p_147423_2_, p_147423_3_, 5, 33, 5, f, f1, f);
        this.field_147425_f = this.field_147432_k.generateNoiseOctaves(this.field_147425_f, p_147423_1_, p_147423_2_, p_147423_3_, 5, 33, 5, f, f1, f);
        p_147423_3_ = 0;
        p_147423_1_ = 0;
        int i = 0;
        int j = 0;

        for (int k = 0; k < 5; ++k) {
            for (int l = 0; l < 5; ++l) {
                float f2 = 0.0F;
                float f3 = 0.0F;
                float f4 = 0.0F;
                final int i1 = 2;
                final BiomeGenBase biomegenbase = this.biomesForGeneration[k + 2 + (l + 2) * 10];

                for (int j1 = -i1; j1 <= i1; ++j1) {
                    for (int k1 = -i1; k1 <= i1; ++k1) {
                        final BiomeGenBase biomegenbase1 = this.biomesForGeneration[k + j1 + 2 + (l + k1 + 2) * 10];
                        float f5 = this.settings.biomeDepthOffSet + biomegenbase1.minHeight * this.settings.biomeDepthWeight;
                        float f6 = this.settings.biomeScaleOffset + biomegenbase1.maxHeight * this.settings.biomeScaleWeight;

                        if (this.field_177475_o == WorldType.AMPLIFIED && f5 > 0.0F) {
                            f5 = 1.0F + f5 * 2.0F;
                            f6 = 1.0F + f6 * 4.0F;
                        }

                        float f7 = this.parabolicField[j1 + 2 + (k1 + 2) * 5] / (f5 + 2.0F);

                        if (biomegenbase1.minHeight > biomegenbase.minHeight) {
                            f7 /= 2.0F;
                        }

                        f2 += f6 * f7;
                        f3 += f5 * f7;
                        f4 += f7;
                    }
                }

                f2 = f2 / f4;
                f3 = f3 / f4;
                f2 = f2 * 0.9F + 0.1F;
                f3 = (f3 * 4.0F - 1.0F) / 8.0F;
                double d7 = this.field_147426_g[j] / 8000.0D;

                if (d7 < 0.0D) {
                    d7 = -d7 * 0.3D;
                }

                d7 = d7 * 3.0D - 2.0D;

                if (d7 < 0.0D) {
                    d7 = d7 / 2.0D;

                    if (d7 < -1.0D) {
                        d7 = -1.0D;
                    }

                    d7 = d7 / 1.4D;
                    d7 = d7 / 2.0D;
                } else {
                    if (d7 > 1.0D) {
                        d7 = 1.0D;
                    }

                    d7 = d7 / 8.0D;
                }

                ++j;
                double d8 = f3;
                final double d9 = f2;
                d8 = d8 + d7 * 0.2D;
                d8 = d8 * (double) this.settings.baseSize / 8.0D;
                final double d0 = (double) this.settings.baseSize + d8 * 4.0D;

                for (int l1 = 0; l1 < 33; ++l1) {
                    double d1 = ((double) l1 - d0) * (double) this.settings.stretchY * 128.0D / 256.0D / d9;

                    if (d1 < 0.0D) {
                        d1 *= 4.0D;
                    }

                    final double d2 = this.field_147428_e[i] / (double) this.settings.lowerLimitScale;
                    final double d3 = this.field_147425_f[i] / (double) this.settings.upperLimitScale;
                    final double d4 = (this.field_147427_d[i] / 10.0D + 1.0D) / 2.0D;
                    double d5 = MathHelper.denormalizeClamp(d2, d3, d4) - d1;

                    if (l1 > 29) {
                        final double d6 = (float) (l1 - 29) / 3.0F;
                        d5 = d5 * (1.0D - d6) + -10.0D * d6;
                    }

                    this.field_147434_q[i] = d5;
                    ++i;
                }
            }
        }
    }

    /**
     * Checks to see if a chunk exists at x, z
     */
    public boolean chunkExists(final int x, final int z) {
        return true;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(final IChunkProvider p_73153_1_, final int p_73153_2_, final int p_73153_3_) {
        BlockFalling.fallInstantly = true;
        final int i = p_73153_2_ * 16;
        final int j = p_73153_3_ * 16;
        BlockPos blockpos = new BlockPos(i, 0, j);
        final BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(blockpos.add(16, 0, 16));
        this.rand.setSeed(this.worldObj.getSeed());
        final long k = this.rand.nextLong() / 2L * 2L + 1L;
        final long l = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long) p_73153_2_ * k + (long) p_73153_3_ * l ^ this.worldObj.getSeed());
        boolean flag = false;
        final ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(p_73153_2_, p_73153_3_);

        if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
            this.mineshaftGenerator.generateStructure(this.worldObj, this.rand, chunkcoordintpair);
        }

        if (this.settings.useVillages && this.mapFeaturesEnabled) {
            flag = this.villageGenerator.generateStructure(this.worldObj, this.rand, chunkcoordintpair);
        }

        if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
            this.strongholdGenerator.generateStructure(this.worldObj, this.rand, chunkcoordintpair);
        }

        if (this.settings.useTemples && this.mapFeaturesEnabled) {
            this.scatteredFeatureGenerator.generateStructure(this.worldObj, this.rand, chunkcoordintpair);
        }

        if (this.settings.useMonuments && this.mapFeaturesEnabled) {
            this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, chunkcoordintpair);
        }

        if (biomegenbase != BiomeGenBase.desert && biomegenbase != BiomeGenBase.desertHills && this.settings.useWaterLakes && !flag && this.rand.nextInt(this.settings.waterLakeChance) == 0) {
            final int i1 = this.rand.nextInt(16) + 8;
            final int j1 = this.rand.nextInt(256);
            final int k1 = this.rand.nextInt(16) + 8;
            (new WorldGenLakes(Blocks.water)).generate(this.worldObj, this.rand, blockpos.add(i1, j1, k1));
        }

        if (!flag && this.rand.nextInt(this.settings.lavaLakeChance / 10) == 0 && this.settings.useLavaLakes) {
            final int i2 = this.rand.nextInt(16) + 8;
            final int l2 = this.rand.nextInt(this.rand.nextInt(248) + 8);
            final int k3 = this.rand.nextInt(16) + 8;

            if (l2 < this.worldObj.func_181545_F() || this.rand.nextInt(this.settings.lavaLakeChance / 8) == 0) {
                (new WorldGenLakes(Blocks.lava)).generate(this.worldObj, this.rand, blockpos.add(i2, l2, k3));
            }
        }

        if (this.settings.useDungeons) {
            for (int j2 = 0; j2 < this.settings.dungeonChance; ++j2) {
                final int i3 = this.rand.nextInt(16) + 8;
                final int l3 = this.rand.nextInt(256);
                final int l1 = this.rand.nextInt(16) + 8;
                (new WorldGenDungeons()).generate(this.worldObj, this.rand, blockpos.add(i3, l3, l1));
            }
        }

        biomegenbase.decorate(this.worldObj, this.rand, new BlockPos(i, 0, j));
        SpawnerAnimals.performWorldGenSpawning(this.worldObj, biomegenbase, i + 8, j + 8, 16, 16, this.rand);
        blockpos = blockpos.add(8, 0, 8);

        for (int k2 = 0; k2 < 16; ++k2) {
            for (int j3 = 0; j3 < 16; ++j3) {
                final BlockPos blockpos1 = this.worldObj.getPrecipitationHeight(blockpos.add(k2, 0, j3));
                final BlockPos blockpos2 = blockpos1.down();

                if (this.worldObj.canBlockFreezeWater(blockpos2)) {
                    this.worldObj.setBlockState(blockpos2, Blocks.ice.getDefaultState(), 2);
                }

                if (this.worldObj.canSnowAt(blockpos1, true)) {
                    this.worldObj.setBlockState(blockpos1, Blocks.snow_layer.getDefaultState(), 2);
                }
            }
        }

        BlockFalling.fallInstantly = false;
    }

    public boolean func_177460_a(final IChunkProvider p_177460_1_, final Chunk p_177460_2_, final int p_177460_3_, final int p_177460_4_) {
        boolean flag = false;

        if (this.settings.useMonuments && this.mapFeaturesEnabled && p_177460_2_.getInhabitedTime() < 3600L) {
            flag |= this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, new ChunkCoordIntPair(p_177460_3_, p_177460_4_));
        }

        return flag;
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(final boolean p_73151_1_, final IProgressUpdate progressCallback) {
        return true;
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unimplemented.
     */
    public void saveExtraData() {
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks() {
        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave() {
        return true;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString() {
        return "RandomLevelSource";
    }

    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(final EnumCreatureType creatureType, final BlockPos pos) {
        final BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(pos);

        if (this.mapFeaturesEnabled) {
            if (creatureType == EnumCreatureType.MONSTER && this.scatteredFeatureGenerator.func_175798_a(pos)) {
                return this.scatteredFeatureGenerator.getScatteredFeatureSpawnList();
            }

            if (creatureType == EnumCreatureType.MONSTER && this.settings.useMonuments && this.oceanMonumentGenerator.func_175796_a(this.worldObj, pos)) {
                return this.oceanMonumentGenerator.func_175799_b();
            }
        }

        return biomegenbase.getSpawnableList(creatureType);
    }

    public BlockPos getStrongholdGen(final World worldIn, final String structureName, final BlockPos position) {
        return "Stronghold".equals(structureName) && this.strongholdGenerator != null ? this.strongholdGenerator.getClosestStrongholdPos(worldIn, position) : null;
    }

    public int getLoadedChunkCount() {
        return 0;
    }

    public void recreateStructures(final Chunk p_180514_1_, final int p_180514_2_, final int p_180514_3_) {
        if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
            this.mineshaftGenerator.generate(this, this.worldObj, p_180514_2_, p_180514_3_, null);
        }

        if (this.settings.useVillages && this.mapFeaturesEnabled) {
            this.villageGenerator.generate(this, this.worldObj, p_180514_2_, p_180514_3_, null);
        }

        if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
            this.strongholdGenerator.generate(this, this.worldObj, p_180514_2_, p_180514_3_, null);
        }

        if (this.settings.useTemples && this.mapFeaturesEnabled) {
            this.scatteredFeatureGenerator.generate(this, this.worldObj, p_180514_2_, p_180514_3_, null);
        }

        if (this.settings.useMonuments && this.mapFeaturesEnabled) {
            this.oceanMonumentGenerator.generate(this, this.worldObj, p_180514_2_, p_180514_3_, null);
        }
    }

    public Chunk provideChunk(final BlockPos blockPosIn) {
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
    }
}
