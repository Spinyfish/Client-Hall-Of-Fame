/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenMesa
extends BiomeGenBase {
    private IBlockState[] field_150621_aC;
    private long field_150622_aD;
    private NoiseGeneratorPerlin field_150623_aE;
    private NoiseGeneratorPerlin field_150624_aF;
    private NoiseGeneratorPerlin field_150625_aG;
    private boolean field_150626_aH;
    private boolean field_150620_aI;

    public BiomeGenMesa(int p_i45380_1_, boolean p_i45380_2_, boolean p_i45380_3_) {
        super(p_i45380_1_);
        this.field_150626_aH = p_i45380_2_;
        this.field_150620_aI = p_i45380_3_;
        this.setDisableRain();
        this.setTemperatureRainfall(2.0f, 0.0f);
        this.spawnableCreatureList.clear();
        this.topBlock = Blocks.sand.getDefaultState().withProperty(BlockSand.VARIANT, BlockSand.EnumType.RED_SAND);
        this.fillerBlock = Blocks.stained_hardened_clay.getDefaultState();
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.deadBushPerChunk = 20;
        this.theBiomeDecorator.reedsPerChunk = 3;
        this.theBiomeDecorator.cactiPerChunk = 5;
        this.theBiomeDecorator.flowersPerChunk = 0;
        this.spawnableCreatureList.clear();
        if (p_i45380_3_) {
            this.theBiomeDecorator.treesPerChunk = 5;
        }
    }

    @Override
    public WorldGenAbstractTree genBigTreeChance(Random rand) {
        return this.worldGeneratorTrees;
    }

    @Override
    public int getFoliageColorAtPos(BlockPos pos) {
        return 10387789;
    }

    @Override
    public int getGrassColorAtPos(BlockPos pos) {
        return 9470285;
    }

    @Override
    public void decorate(World worldIn, Random rand, BlockPos pos) {
        super.decorate(worldIn, rand, pos);
    }

    @Override
    public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int p_180622_4_, int p_180622_5_, double p_180622_6_) {
        if (this.field_150621_aC == null || this.field_150622_aD != worldIn.getSeed()) {
            this.func_150619_a(worldIn.getSeed());
        }
        if (this.field_150623_aE == null || this.field_150624_aF == null || this.field_150622_aD != worldIn.getSeed()) {
            Random random = new Random(this.field_150622_aD);
            this.field_150623_aE = new NoiseGeneratorPerlin(random, 4);
            this.field_150624_aF = new NoiseGeneratorPerlin(random, 1);
        }
        this.field_150622_aD = worldIn.getSeed();
        double d4 = 0.0;
        if (this.field_150626_aH) {
            int i = (p_180622_4_ & 0xFFFFFFF0) + (p_180622_5_ & 0xF);
            int j = (p_180622_5_ & 0xFFFFFFF0) + (p_180622_4_ & 0xF);
            double d0 = Math.min(Math.abs(p_180622_6_), this.field_150623_aE.func_151601_a((double)i * 0.25, (double)j * 0.25));
            if (d0 > 0.0) {
                d4 = d0 * d0 * 2.5;
                double d1 = 0.001953125;
                double d2 = Math.abs(this.field_150624_aF.func_151601_a((double)i * d1, (double)j * d1));
                double d3 = Math.ceil(d2 * 50.0) + 14.0;
                if (d4 > d3) {
                    d4 = d3;
                }
                d4 += 64.0;
            }
        }
        int j1 = p_180622_4_ & 0xF;
        int k1 = p_180622_5_ & 0xF;
        int l1 = worldIn.func_181545_F();
        IBlockState iblockstate = Blocks.stained_hardened_clay.getDefaultState();
        IBlockState iblockstate3 = this.fillerBlock;
        int k = (int)(p_180622_6_ / 3.0 + 3.0 + rand.nextDouble() * 0.25);
        boolean flag = Math.cos(p_180622_6_ / 3.0 * Math.PI) > 0.0;
        int l = -1;
        boolean flag1 = false;
        for (int i1 = 255; i1 >= 0; --i1) {
            if (chunkPrimerIn.getBlockState(k1, i1, j1).getBlock().getMaterial() == Material.air && i1 < (int)d4) {
                chunkPrimerIn.setBlockState(k1, i1, j1, Blocks.stone.getDefaultState());
            }
            if (i1 <= rand.nextInt(5)) {
                chunkPrimerIn.setBlockState(k1, i1, j1, Blocks.bedrock.getDefaultState());
                continue;
            }
            IBlockState iblockstate1 = chunkPrimerIn.getBlockState(k1, i1, j1);
            if (iblockstate1.getBlock().getMaterial() == Material.air) {
                l = -1;
                continue;
            }
            if (iblockstate1.getBlock() != Blocks.stone) continue;
            if (l == -1) {
                flag1 = false;
                if (k <= 0) {
                    iblockstate = null;
                    iblockstate3 = Blocks.stone.getDefaultState();
                } else if (i1 >= l1 - 4 && i1 <= l1 + 1) {
                    iblockstate = Blocks.stained_hardened_clay.getDefaultState();
                    iblockstate3 = this.fillerBlock;
                }
                if (i1 < l1 && (iblockstate == null || iblockstate.getBlock().getMaterial() == Material.air)) {
                    iblockstate = Blocks.water.getDefaultState();
                }
                l = k + Math.max(0, i1 - l1);
                if (i1 < l1 - 1) {
                    chunkPrimerIn.setBlockState(k1, i1, j1, iblockstate3);
                    if (iblockstate3.getBlock() != Blocks.stained_hardened_clay) continue;
                    chunkPrimerIn.setBlockState(k1, i1, j1, iblockstate3.getBlock().getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE));
                    continue;
                }
                if (this.field_150620_aI && i1 > 86 + k * 2) {
                    if (flag) {
                        chunkPrimerIn.setBlockState(k1, i1, j1, Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT));
                        continue;
                    }
                    chunkPrimerIn.setBlockState(k1, i1, j1, Blocks.grass.getDefaultState());
                    continue;
                }
                if (i1 <= l1 + 3 + k) {
                    chunkPrimerIn.setBlockState(k1, i1, j1, this.topBlock);
                    flag1 = true;
                    continue;
                }
                IBlockState iblockstate4 = i1 >= 64 && i1 <= 127 ? (flag ? Blocks.hardened_clay.getDefaultState() : this.func_180629_a(p_180622_4_, i1, p_180622_5_)) : Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE);
                chunkPrimerIn.setBlockState(k1, i1, j1, iblockstate4);
                continue;
            }
            if (l <= 0) continue;
            --l;
            if (flag1) {
                chunkPrimerIn.setBlockState(k1, i1, j1, Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE));
                continue;
            }
            IBlockState iblockstate2 = this.func_180629_a(p_180622_4_, i1, p_180622_5_);
            chunkPrimerIn.setBlockState(k1, i1, j1, iblockstate2);
        }
    }

    private void func_150619_a(long p_150619_1_) {
        this.field_150621_aC = new IBlockState[64];
        Arrays.fill(this.field_150621_aC, Blocks.hardened_clay.getDefaultState());
        Random random = new Random(p_150619_1_);
        this.field_150625_aG = new NoiseGeneratorPerlin(random, 1);
        for (int l1 = 0; l1 < 64; ++l1) {
            if ((l1 += random.nextInt(5) + 1) >= 64) continue;
            this.field_150621_aC[l1] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE);
        }
        int i2 = random.nextInt(4) + 2;
        for (int i = 0; i < i2; ++i) {
            int j = random.nextInt(3) + 1;
            int k = random.nextInt(64);
            for (int l = 0; k + l < 64 && l < j; ++l) {
                this.field_150621_aC[k + l] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW);
            }
        }
        int j2 = random.nextInt(4) + 2;
        for (int k2 = 0; k2 < j2; ++k2) {
            int i3 = random.nextInt(3) + 2;
            int l3 = random.nextInt(64);
            for (int i1 = 0; l3 + i1 < 64 && i1 < i3; ++i1) {
                this.field_150621_aC[l3 + i1] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BROWN);
            }
        }
        int l2 = random.nextInt(4) + 2;
        for (int j3 = 0; j3 < l2; ++j3) {
            int i4 = random.nextInt(3) + 1;
            int k4 = random.nextInt(64);
            for (int j1 = 0; k4 + j1 < 64 && j1 < i4; ++j1) {
                this.field_150621_aC[k4 + j1] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED);
            }
        }
        int k3 = random.nextInt(3) + 3;
        int j4 = 0;
        for (int l4 = 0; l4 < k3; ++l4) {
            int i5 = 1;
            j4 += random.nextInt(16) + 4;
            for (int k1 = 0; j4 + k1 < 64 && k1 < i5; ++k1) {
                this.field_150621_aC[j4 + k1] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE);
                if (j4 + k1 > 1 && random.nextBoolean()) {
                    this.field_150621_aC[j4 + k1 - 1] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
                }
                if (j4 + k1 >= 63 || !random.nextBoolean()) continue;
                this.field_150621_aC[j4 + k1 + 1] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
            }
        }
    }

    private IBlockState func_180629_a(int p_180629_1_, int p_180629_2_, int p_180629_3_) {
        int i = (int)Math.round(this.field_150625_aG.func_151601_a((double)p_180629_1_ * 1.0 / 512.0, (double)p_180629_1_ * 1.0 / 512.0) * 2.0);
        return this.field_150621_aC[(p_180629_2_ + i + 64) % 64];
    }

    @Override
    protected BiomeGenBase createMutatedBiome(int p_180277_1_) {
        boolean flag = this.biomeID == BiomeGenBase.mesa.biomeID;
        BiomeGenMesa biomegenmesa = new BiomeGenMesa(p_180277_1_, flag, this.field_150620_aI);
        if (!flag) {
            biomegenmesa.setHeight(height_LowHills);
            biomegenmesa.setBiomeName(this.biomeName + " M");
        } else {
            biomegenmesa.setBiomeName(this.biomeName + " (Bryce)");
        }
        biomegenmesa.func_150557_a(this.color, true);
        return biomegenmesa;
    }
}

