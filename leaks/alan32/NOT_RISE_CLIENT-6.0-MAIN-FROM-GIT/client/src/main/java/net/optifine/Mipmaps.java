package net.optifine;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.src.Config;
import net.optifine.util.TextureUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Mipmaps {
    private final String iconName;
    private final int width;
    private final int height;
    private final int[] data;
    private final boolean direct;
    private final int[][] mipmapDatas;
    private IntBuffer[] mipmapBuffers;
    private final Dimension[] mipmapDimensions;

    public Mipmaps(final String iconName, final int width, final int height, final int[] data, final boolean direct) {
        this.iconName = iconName;
        this.width = width;
        this.height = height;
        this.data = data;
        this.direct = direct;
        this.mipmapDimensions = makeMipmapDimensions(width, height, iconName);
        this.mipmapDatas = generateMipMapData(data, width, height, this.mipmapDimensions);

        if (direct) {
            this.mipmapBuffers = makeMipmapBuffers(this.mipmapDimensions, this.mipmapDatas);
        }
    }

    public static Dimension[] makeMipmapDimensions(final int width, final int height, final String iconName) {
        final int i = TextureUtils.ceilPowerOfTwo(width);
        final int j = TextureUtils.ceilPowerOfTwo(height);

        if (i == width && j == height) {
            final List list = new ArrayList();
            int k = i;
            int l = j;

            while (true) {
                k /= 2;
                l /= 2;

                if (k <= 0 && l <= 0) {
                    final Dimension[] adimension = (Dimension[]) list.toArray(new Dimension[list.size()]);
                    return adimension;
                }

                if (k <= 0) {
                    k = 1;
                }

                if (l <= 0) {
                    l = 1;
                }

                final int i1 = k * l * 4;
                final Dimension dimension = new Dimension(k, l);
                list.add(dimension);
            }
        } else {
            Config.warn("Mipmaps not possible (power of 2 dimensions needed), texture: " + iconName + ", dim: " + width + "x" + height);
            return new Dimension[0];
        }
    }

    public static int[][] generateMipMapData(final int[] data, final int width, final int height, final Dimension[] mipmapDimensions) {
        int[] aint = data;
        int i = width;
        boolean flag = true;
        final int[][] aint1 = new int[mipmapDimensions.length][];

        for (int j = 0; j < mipmapDimensions.length; ++j) {
            final Dimension dimension = mipmapDimensions[j];
            final int k = dimension.width;
            final int l = dimension.height;
            final int[] aint2 = new int[k * l];
            aint1[j] = aint2;
            final int i1 = j + 1;

            if (flag) {
                for (int j1 = 0; j1 < k; ++j1) {
                    for (int k1 = 0; k1 < l; ++k1) {
                        final int l1 = aint[j1 * 2 + 0 + (k1 * 2 + 0) * i];
                        final int i2 = aint[j1 * 2 + 1 + (k1 * 2 + 0) * i];
                        final int j2 = aint[j1 * 2 + 1 + (k1 * 2 + 1) * i];
                        final int k2 = aint[j1 * 2 + 0 + (k1 * 2 + 1) * i];
                        final int l2 = alphaBlend(l1, i2, j2, k2);
                        aint2[j1 + k1 * k] = l2;
                    }
                }
            }

            aint = aint2;
            i = k;

            if (k <= 1 || l <= 1) {
                flag = false;
            }
        }

        return aint1;
    }

    public static int alphaBlend(final int c1, final int c2, final int c3, final int c4) {
        final int i = alphaBlend(c1, c2);
        final int j = alphaBlend(c3, c4);
        final int k = alphaBlend(i, j);
        return k;
    }

    private static int alphaBlend(int c1, int c2) {
        int i = (c1 & -16777216) >> 24 & 255;
        int j = (c2 & -16777216) >> 24 & 255;
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

        final int l = (c1 >> 16 & 255) * i;
        final int i1 = (c1 >> 8 & 255) * i;
        final int j1 = (c1 & 255) * i;
        final int k1 = (c2 >> 16 & 255) * j;
        final int l1 = (c2 >> 8 & 255) * j;
        final int i2 = (c2 & 255) * j;
        final int j2 = (l + k1) / (i + j);
        final int k2 = (i1 + l1) / (i + j);
        final int l2 = (j1 + i2) / (i + j);
        return k << 24 | j2 << 16 | k2 << 8 | l2;
    }

    private int averageColor(final int i, final int j) {
        final int k = (i & -16777216) >> 24 & 255;
        final int p = (j & -16777216) >> 24 & 255;
        return (k + j >> 1 << 24) + ((k & 16711422) + (p & 16711422) >> 1);
    }

    public static IntBuffer[] makeMipmapBuffers(final Dimension[] mipmapDimensions, final int[][] mipmapDatas) {
        if (mipmapDimensions == null) {
            return null;
        } else {
            final IntBuffer[] aintbuffer = new IntBuffer[mipmapDimensions.length];

            for (int i = 0; i < mipmapDimensions.length; ++i) {
                final Dimension dimension = mipmapDimensions[i];
                final int j = dimension.width * dimension.height;
                final IntBuffer intbuffer = GLAllocation.createDirectIntBuffer(j);
                final int[] aint = mipmapDatas[i];
                intbuffer.clear();
                intbuffer.put(aint);
                intbuffer.clear();
                aintbuffer[i] = intbuffer;
            }

            return aintbuffer;
        }
    }

    public static void allocateMipmapTextures(final int width, final int height, final String name) {
        final Dimension[] adimension = makeMipmapDimensions(width, height, name);

        for (int i = 0; i < adimension.length; ++i) {
            final Dimension dimension = adimension[i];
            final int j = dimension.width;
            final int k = dimension.height;
            final int l = i + 1;
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, l, GL11.GL_RGBA, j, k, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (ByteBuffer) null);
        }
    }
}
