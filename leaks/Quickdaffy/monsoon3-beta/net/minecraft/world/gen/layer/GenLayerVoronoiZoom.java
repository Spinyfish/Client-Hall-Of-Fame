/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerVoronoiZoom
extends GenLayer {
    public GenLayerVoronoiZoom(long p_i2133_1_, GenLayer p_i2133_3_) {
        super(p_i2133_1_);
        this.parent = p_i2133_3_;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int i = (areaX -= 2) >> 2;
        int j = (areaY -= 2) >> 2;
        int k = (areaWidth >> 2) + 2;
        int l = (areaHeight >> 2) + 2;
        int[] aint = this.parent.getInts(i, j, k, l);
        int i1 = k - 1 << 2;
        int j1 = l - 1 << 2;
        int[] aint1 = IntCache.getIntCache(i1 * j1);
        for (int k1 = 0; k1 < l - 1; ++k1) {
            int l1;
            int i2 = aint[l1 + 0 + (k1 + 0) * k];
            int j2 = aint[l1 + 0 + (k1 + 1) * k];
            for (l1 = 0; l1 < k - 1; ++l1) {
                double d0 = 3.6;
                this.initChunkSeed(l1 + i << 2, k1 + j << 2);
                double d1 = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
                double d2 = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
                this.initChunkSeed(l1 + i + 1 << 2, k1 + j << 2);
                double d3 = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
                double d4 = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
                this.initChunkSeed(l1 + i << 2, k1 + j + 1 << 2);
                double d5 = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
                double d6 = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
                this.initChunkSeed(l1 + i + 1 << 2, k1 + j + 1 << 2);
                double d7 = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
                double d8 = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
                int k2 = aint[l1 + 1 + (k1 + 0) * k] & 0xFF;
                int l2 = aint[l1 + 1 + (k1 + 1) * k] & 0xFF;
                for (int i3 = 0; i3 < 4; ++i3) {
                    int j3 = ((k1 << 2) + i3) * i1 + (l1 << 2);
                    for (int k3 = 0; k3 < 4; ++k3) {
                        double d9 = ((double)i3 - d2) * ((double)i3 - d2) + ((double)k3 - d1) * ((double)k3 - d1);
                        double d10 = ((double)i3 - d4) * ((double)i3 - d4) + ((double)k3 - d3) * ((double)k3 - d3);
                        double d11 = ((double)i3 - d6) * ((double)i3 - d6) + ((double)k3 - d5) * ((double)k3 - d5);
                        double d12 = ((double)i3 - d8) * ((double)i3 - d8) + ((double)k3 - d7) * ((double)k3 - d7);
                        aint1[j3++] = d9 < d10 && d9 < d11 && d9 < d12 ? i2 : (d10 < d9 && d10 < d11 && d10 < d12 ? k2 : (d11 < d9 && d11 < d10 && d11 < d12 ? j2 : l2));
                    }
                }
                i2 = k2;
                j2 = l2;
            }
        }
        int[] aint2 = IntCache.getIntCache(areaWidth * areaHeight);
        for (int l3 = 0; l3 < areaHeight; ++l3) {
            System.arraycopy(aint1, (l3 + (areaY & 3)) * i1 + (areaX & 3), aint2, l3 * areaWidth, areaWidth);
        }
        return aint2;
    }
}

