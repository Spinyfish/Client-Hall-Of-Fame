/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.optifine;

import java.awt.Dimension;
import java.nio.IntBuffer;
import java.util.ArrayList;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.src.Config;
import net.optifine.util.TextureUtils;
import org.lwjgl.opengl.GL11;

public class Mipmaps {
    private final String iconName;
    private final int width;
    private final int height;
    private final int[] data;
    private final boolean direct;
    private int[][] mipmapDatas;
    private IntBuffer[] mipmapBuffers;
    private Dimension[] mipmapDimensions;

    public Mipmaps(String iconName, int width, int height, int[] data, boolean direct) {
        this.iconName = iconName;
        this.width = width;
        this.height = height;
        this.data = data;
        this.direct = direct;
        this.mipmapDimensions = Mipmaps.makeMipmapDimensions(width, height, iconName);
        this.mipmapDatas = Mipmaps.generateMipMapData(data, width, height, this.mipmapDimensions);
        if (direct) {
            this.mipmapBuffers = Mipmaps.makeMipmapBuffers(this.mipmapDimensions, this.mipmapDatas);
        }
    }

    public static Dimension[] makeMipmapDimensions(int width, int height, String iconName) {
        int i = TextureUtils.ceilPowerOfTwo(width);
        int j = TextureUtils.ceilPowerOfTwo(height);
        if (i == width && j == height) {
            ArrayList<Dimension> list = new ArrayList<Dimension>();
            int k = i;
            int l = j;
            while (true) {
                if ((k /= 2) <= 0 && (l /= 2) <= 0) {
                    Dimension[] adimension = list.toArray(new Dimension[list.size()]);
                    return adimension;
                }
                if (k <= 0) {
                    k = 1;
                }
                if (l <= 0) {
                    l = 1;
                }
                int i1 = k * l * 4;
                Dimension dimension = new Dimension(k, l);
                list.add(dimension);
            }
        }
        Config.warn("Mipmaps not possible (power of 2 dimensions needed), texture: " + iconName + ", dim: " + width + "x" + height);
        return new Dimension[0];
    }

    public static int[][] generateMipMapData(int[] data, int width, int height, Dimension[] mipmapDimensions) {
        int[] aint = data;
        int i = width;
        boolean flag = true;
        int[][] aint1 = new int[mipmapDimensions.length][];
        for (int j = 0; j < mipmapDimensions.length; ++j) {
            Dimension dimension = mipmapDimensions[j];
            int k = dimension.width;
            int l = dimension.height;
            int[] aint2 = new int[k * l];
            aint1[j] = aint2;
            int i1 = j + 1;
            if (flag) {
                for (int j1 = 0; j1 < k; ++j1) {
                    for (int k1 = 0; k1 < l; ++k1) {
                        int l2;
                        int l1 = aint[j1 * 2 + 0 + (k1 * 2 + 0) * i];
                        int i2 = aint[j1 * 2 + 1 + (k1 * 2 + 0) * i];
                        int j2 = aint[j1 * 2 + 1 + (k1 * 2 + 1) * i];
                        int k2 = aint[j1 * 2 + 0 + (k1 * 2 + 1) * i];
                        aint2[j1 + k1 * k] = l2 = Mipmaps.alphaBlend(l1, i2, j2, k2);
                    }
                }
            }
            aint = aint2;
            i = k;
            if (k > 1 && l > 1) continue;
            flag = false;
        }
        return aint1;
    }

    public static int alphaBlend(int c1, int c2, int c3, int c4) {
        int i = Mipmaps.alphaBlend(c1, c2);
        int j = Mipmaps.alphaBlend(c3, c4);
        int k = Mipmaps.alphaBlend(i, j);
        return k;
    }

    private static int alphaBlend(int c1, int c2) {
        int i = (c1 & 0xFF000000) >> 24 & 0xFF;
        int j = (c2 & 0xFF000000) >> 24 & 0xFF;
        int k = (i + j) / 2;
        if (i == 0 && j == 0) {
            i = 1;
            j = 1;
        } else {
            if (i == 0) {
                c1 = c2;
                k /= 2;
            }
            if (j == 0) {
                c2 = c1;
                k /= 2;
            }
        }
        int l = (c1 >> 16 & 0xFF) * i;
        int i1 = (c1 >> 8 & 0xFF) * i;
        int j1 = (c1 & 0xFF) * i;
        int k1 = (c2 >> 16 & 0xFF) * j;
        int l1 = (c2 >> 8 & 0xFF) * j;
        int i2 = (c2 & 0xFF) * j;
        int j2 = (l + k1) / (i + j);
        int k2 = (i1 + l1) / (i + j);
        int l2 = (j1 + i2) / (i + j);
        return k << 24 | j2 << 16 | k2 << 8 | l2;
    }

    private int averageColor(int i, int j) {
        int k = (i & 0xFF000000) >> 24 & 0xFF;
        int p = (j & 0xFF000000) >> 24 & 0xFF;
        return (k + j >> 1 << 24) + ((k & 0xFEFEFE) + (p & 0xFEFEFE) >> 1);
    }

    public static IntBuffer[] makeMipmapBuffers(Dimension[] mipmapDimensions, int[][] mipmapDatas) {
        if (mipmapDimensions == null) {
            return null;
        }
        IntBuffer[] aintbuffer = new IntBuffer[mipmapDimensions.length];
        for (int i = 0; i < mipmapDimensions.length; ++i) {
            Dimension dimension = mipmapDimensions[i];
            int j = dimension.width * dimension.height;
            IntBuffer intbuffer = GLAllocation.createDirectIntBuffer(j);
            int[] aint = mipmapDatas[i];
            intbuffer.clear();
            intbuffer.put(aint);
            intbuffer.clear();
            aintbuffer[i] = intbuffer;
        }
        return aintbuffer;
    }

    public static void allocateMipmapTextures(int width, int height, String name) {
        Dimension[] adimension = Mipmaps.makeMipmapDimensions(width, height, name);
        for (int i = 0; i < adimension.length; ++i) {
            Dimension dimension = adimension[i];
            int j = dimension.width;
            int k = dimension.height;
            int l = i + 1;
            GL11.glTexImage2D((int)3553, (int)l, (int)6408, (int)j, (int)k, (int)0, (int)32993, (int)33639, (IntBuffer)null);
        }
    }
}

