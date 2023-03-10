/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenLayerHills
extends GenLayer {
    private static final Logger logger = LogManager.getLogger();
    private GenLayer field_151628_d;

    public GenLayerHills(long p_i45479_1_, GenLayer p_i45479_3_, GenLayer p_i45479_4_) {
        super(p_i45479_1_);
        this.parent = p_i45479_3_;
        this.field_151628_d = p_i45479_4_;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] aint = this.parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
        int[] aint1 = this.field_151628_d.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
        int[] aint2 = IntCache.getIntCache(areaWidth * areaHeight);
        for (int i = 0; i < areaHeight; ++i) {
            for (int j = 0; j < areaWidth; ++j) {
                boolean flag;
                this.initChunkSeed(j + areaX, i + areaY);
                int k = aint[j + 1 + (i + 1) * (areaWidth + 2)];
                int l = aint1[j + 1 + (i + 1) * (areaWidth + 2)];
                boolean bl = flag = (l - 2) % 29 == 0;
                if (k > 255) {
                    logger.debug("old! " + k);
                }
                if (k != 0 && l >= 2 && (l - 2) % 29 == 1 && k < 128) {
                    if (BiomeGenBase.getBiome(k + 128) != null) {
                        aint2[j + i * areaWidth] = k + 128;
                        continue;
                    }
                    aint2[j + i * areaWidth] = k;
                    continue;
                }
                if (this.nextInt(3) != 0 && !flag) {
                    aint2[j + i * areaWidth] = k;
                    continue;
                }
                int i1 = k;
                if (k == BiomeGenBase.desert.biomeID) {
                    i1 = BiomeGenBase.desertHills.biomeID;
                } else if (k == BiomeGenBase.forest.biomeID) {
                    i1 = BiomeGenBase.forestHills.biomeID;
                } else if (k == BiomeGenBase.birchForest.biomeID) {
                    i1 = BiomeGenBase.birchForestHills.biomeID;
                } else if (k == BiomeGenBase.roofedForest.biomeID) {
                    i1 = BiomeGenBase.plains.biomeID;
                } else if (k == BiomeGenBase.taiga.biomeID) {
                    i1 = BiomeGenBase.taigaHills.biomeID;
                } else if (k == BiomeGenBase.megaTaiga.biomeID) {
                    i1 = BiomeGenBase.megaTaigaHills.biomeID;
                } else if (k == BiomeGenBase.coldTaiga.biomeID) {
                    i1 = BiomeGenBase.coldTaigaHills.biomeID;
                } else if (k == BiomeGenBase.plains.biomeID) {
                    i1 = this.nextInt(3) == 0 ? BiomeGenBase.forestHills.biomeID : BiomeGenBase.forest.biomeID;
                } else if (k == BiomeGenBase.icePlains.biomeID) {
                    i1 = BiomeGenBase.iceMountains.biomeID;
                } else if (k == BiomeGenBase.jungle.biomeID) {
                    i1 = BiomeGenBase.jungleHills.biomeID;
                } else if (k == BiomeGenBase.ocean.biomeID) {
                    i1 = BiomeGenBase.deepOcean.biomeID;
                } else if (k == BiomeGenBase.extremeHills.biomeID) {
                    i1 = BiomeGenBase.extremeHillsPlus.biomeID;
                } else if (k == BiomeGenBase.savanna.biomeID) {
                    i1 = BiomeGenBase.savannaPlateau.biomeID;
                } else if (GenLayerHills.biomesEqualOrMesaPlateau(k, BiomeGenBase.mesaPlateau_F.biomeID)) {
                    i1 = BiomeGenBase.mesa.biomeID;
                } else if (k == BiomeGenBase.deepOcean.biomeID && this.nextInt(3) == 0) {
                    int j1 = this.nextInt(2);
                    i1 = j1 == 0 ? BiomeGenBase.plains.biomeID : BiomeGenBase.forest.biomeID;
                }
                if (flag && i1 != k) {
                    i1 = BiomeGenBase.getBiome(i1 + 128) != null ? (i1 += 128) : k;
                }
                if (i1 == k) {
                    aint2[j + i * areaWidth] = k;
                    continue;
                }
                int k2 = aint[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
                int k1 = aint[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
                int l1 = aint[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
                int i2 = aint[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
                int j2 = 0;
                if (GenLayerHills.biomesEqualOrMesaPlateau(k2, k)) {
                    ++j2;
                }
                if (GenLayerHills.biomesEqualOrMesaPlateau(k1, k)) {
                    ++j2;
                }
                if (GenLayerHills.biomesEqualOrMesaPlateau(l1, k)) {
                    ++j2;
                }
                if (GenLayerHills.biomesEqualOrMesaPlateau(i2, k)) {
                    ++j2;
                }
                aint2[j + i * areaWidth] = j2 >= 3 ? i1 : k;
            }
        }
        return aint2;
    }
}

