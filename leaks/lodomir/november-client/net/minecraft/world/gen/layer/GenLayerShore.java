/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenJungle;
import net.minecraft.world.biome.BiomeGenMesa;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerShore
extends GenLayer {
    public GenLayerShore(long p_i2130_1_, GenLayer p_i2130_3_) {
        super(p_i2130_1_);
        this.parent = p_i2130_3_;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] aint = this.parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
        int[] aint1 = IntCache.getIntCache(areaWidth * areaHeight);
        for (int i = 0; i < areaHeight; ++i) {
            for (int j = 0; j < areaWidth; ++j) {
                this.initChunkSeed(j + areaX, i + areaY);
                int k = aint[j + 1 + (i + 1) * (areaWidth + 2)];
                BiomeGenBase biomegenbase = BiomeGenBase.getBiome(k);
                if (k == BiomeGenBase.mushroomIsland.biomeID) {
                    int j2 = aint[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
                    int i3 = aint[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
                    int l3 = aint[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
                    int k4 = aint[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
                    if (j2 != BiomeGenBase.ocean.biomeID && i3 != BiomeGenBase.ocean.biomeID && l3 != BiomeGenBase.ocean.biomeID && k4 != BiomeGenBase.ocean.biomeID) {
                        aint1[j + i * areaWidth] = k;
                        continue;
                    }
                    aint1[j + i * areaWidth] = BiomeGenBase.mushroomIslandShore.biomeID;
                    continue;
                }
                if (biomegenbase != null && biomegenbase.getBiomeClass() == BiomeGenJungle.class) {
                    int i2 = aint[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
                    int l2 = aint[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
                    int k3 = aint[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
                    int j4 = aint[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
                    if (this.func_151631_c(i2) && this.func_151631_c(l2) && this.func_151631_c(k3) && this.func_151631_c(j4)) {
                        if (!(GenLayerShore.isBiomeOceanic(i2) || GenLayerShore.isBiomeOceanic(l2) || GenLayerShore.isBiomeOceanic(k3) || GenLayerShore.isBiomeOceanic(j4))) {
                            aint1[j + i * areaWidth] = k;
                            continue;
                        }
                        aint1[j + i * areaWidth] = BiomeGenBase.beach.biomeID;
                        continue;
                    }
                    aint1[j + i * areaWidth] = BiomeGenBase.jungleEdge.biomeID;
                    continue;
                }
                if (k != BiomeGenBase.extremeHills.biomeID && k != BiomeGenBase.extremeHillsPlus.biomeID && k != BiomeGenBase.extremeHillsEdge.biomeID) {
                    if (biomegenbase != null && biomegenbase.isSnowyBiome()) {
                        this.func_151632_a(aint, aint1, j, i, areaWidth, k, BiomeGenBase.coldBeach.biomeID);
                        continue;
                    }
                    if (k != BiomeGenBase.mesa.biomeID && k != BiomeGenBase.mesaPlateau_F.biomeID) {
                        if (k != BiomeGenBase.ocean.biomeID && k != BiomeGenBase.deepOcean.biomeID && k != BiomeGenBase.river.biomeID && k != BiomeGenBase.swampland.biomeID) {
                            int l1 = aint[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
                            int k2 = aint[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
                            int j3 = aint[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
                            int i4 = aint[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
                            if (!(GenLayerShore.isBiomeOceanic(l1) || GenLayerShore.isBiomeOceanic(k2) || GenLayerShore.isBiomeOceanic(j3) || GenLayerShore.isBiomeOceanic(i4))) {
                                aint1[j + i * areaWidth] = k;
                                continue;
                            }
                            aint1[j + i * areaWidth] = BiomeGenBase.beach.biomeID;
                            continue;
                        }
                        aint1[j + i * areaWidth] = k;
                        continue;
                    }
                    int l = aint[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
                    int i1 = aint[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
                    int j1 = aint[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
                    int k1 = aint[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
                    if (!(GenLayerShore.isBiomeOceanic(l) || GenLayerShore.isBiomeOceanic(i1) || GenLayerShore.isBiomeOceanic(j1) || GenLayerShore.isBiomeOceanic(k1))) {
                        if (this.func_151633_d(l) && this.func_151633_d(i1) && this.func_151633_d(j1) && this.func_151633_d(k1)) {
                            aint1[j + i * areaWidth] = k;
                            continue;
                        }
                        aint1[j + i * areaWidth] = BiomeGenBase.desert.biomeID;
                        continue;
                    }
                    aint1[j + i * areaWidth] = k;
                    continue;
                }
                this.func_151632_a(aint, aint1, j, i, areaWidth, k, BiomeGenBase.stoneBeach.biomeID);
            }
        }
        return aint1;
    }

    private void func_151632_a(int[] p_151632_1_, int[] p_151632_2_, int p_151632_3_, int p_151632_4_, int p_151632_5_, int p_151632_6_, int p_151632_7_) {
        if (GenLayerShore.isBiomeOceanic(p_151632_6_)) {
            p_151632_2_[p_151632_3_ + p_151632_4_ * p_151632_5_] = p_151632_6_;
        } else {
            int i = p_151632_1_[p_151632_3_ + 1 + (p_151632_4_ + 1 - 1) * (p_151632_5_ + 2)];
            int j = p_151632_1_[p_151632_3_ + 1 + 1 + (p_151632_4_ + 1) * (p_151632_5_ + 2)];
            int k = p_151632_1_[p_151632_3_ + 1 - 1 + (p_151632_4_ + 1) * (p_151632_5_ + 2)];
            int l = p_151632_1_[p_151632_3_ + 1 + (p_151632_4_ + 1 + 1) * (p_151632_5_ + 2)];
            p_151632_2_[p_151632_3_ + p_151632_4_ * p_151632_5_] = !GenLayerShore.isBiomeOceanic(i) && !GenLayerShore.isBiomeOceanic(j) && !GenLayerShore.isBiomeOceanic(k) && !GenLayerShore.isBiomeOceanic(l) ? p_151632_6_ : p_151632_7_;
        }
    }

    private boolean func_151631_c(int p_151631_1_) {
        return BiomeGenBase.getBiome(p_151631_1_) != null && BiomeGenBase.getBiome(p_151631_1_).getBiomeClass() == BiomeGenJungle.class ? true : p_151631_1_ == BiomeGenBase.jungleEdge.biomeID || p_151631_1_ == BiomeGenBase.jungle.biomeID || p_151631_1_ == BiomeGenBase.jungleHills.biomeID || p_151631_1_ == BiomeGenBase.forest.biomeID || p_151631_1_ == BiomeGenBase.taiga.biomeID || GenLayerShore.isBiomeOceanic(p_151631_1_);
    }

    private boolean func_151633_d(int p_151633_1_) {
        return BiomeGenBase.getBiome(p_151633_1_) instanceof BiomeGenMesa;
    }
}

