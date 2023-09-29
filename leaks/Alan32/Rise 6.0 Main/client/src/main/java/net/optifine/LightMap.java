package net.optifine;

import net.minecraft.src.Config;
import net.minecraft.world.World;

public class LightMap {
    private CustomColormap lightMapRgb = null;
    private final float[][] sunRgbs = new float[16][3];
    private final float[][] torchRgbs = new float[16][3];

    public LightMap(final CustomColormap lightMapRgb) {
        this.lightMapRgb = lightMapRgb;
    }

    public CustomColormap getColormap() {
        return this.lightMapRgb;
    }

    public boolean updateLightmap(final World world, final float torchFlickerX, final int[] lmColors, final boolean nightvision) {
        if (this.lightMapRgb == null) {
            return false;
        } else {
            final int i = this.lightMapRgb.getHeight();

            if (nightvision && i < 64) {
                return false;
            } else {
                final int j = this.lightMapRgb.getWidth();

                if (j < 16) {
                    warn("Invalid lightmap width: " + j);
                    this.lightMapRgb = null;
                    return false;
                } else {
                    int k = 0;

                    if (nightvision) {
                        k = j * 16 * 2;
                    }

                    float f = 1.1666666F * (world.getSunBrightness(1.0F) - 0.2F);

                    if (world.getLastLightningBolt() > 0) {
                        f = 1.0F;
                    }

                    f = Config.limitTo1(f);
                    final float f1 = f * (float) (j - 1);
                    final float f2 = Config.limitTo1(torchFlickerX + 0.5F) * (float) (j - 1);
                    final float f3 = Config.limitTo1(Config.getGameSettings().gammaSetting);
                    final boolean flag = f3 > 1.0E-4F;
                    final float[][] afloat = this.lightMapRgb.getColorsRgb();
                    this.getLightMapColumn(afloat, f1, k, j, this.sunRgbs);
                    this.getLightMapColumn(afloat, f2, k + 16 * j, j, this.torchRgbs);
                    final float[] afloat1 = new float[3];

                    for (int l = 0; l < 16; ++l) {
                        for (int i1 = 0; i1 < 16; ++i1) {
                            for (int j1 = 0; j1 < 3; ++j1) {
                                float f4 = Config.limitTo1(this.sunRgbs[l][j1] + this.torchRgbs[i1][j1]);

                                if (flag) {
                                    float f5 = 1.0F - f4;
                                    f5 = 1.0F - f5 * f5 * f5 * f5;
                                    f4 = f3 * f5 + (1.0F - f3) * f4;
                                }

                                afloat1[j1] = f4;
                            }

                            final int k1 = (int) (afloat1[0] * 255.0F);
                            final int l1 = (int) (afloat1[1] * 255.0F);
                            final int i2 = (int) (afloat1[2] * 255.0F);
                            lmColors[l * 16 + i1] = -16777216 | k1 << 16 | l1 << 8 | i2;
                        }
                    }

                    return true;
                }
            }
        }
    }

    private void getLightMapColumn(final float[][] origMap, final float x, final int offset, final int width, final float[][] colRgb) {
        final int i = (int) Math.floor(x);
        final int j = (int) Math.ceil(x);

        if (i == j) {
            for (int i1 = 0; i1 < 16; ++i1) {
                final float[] afloat3 = origMap[offset + i1 * width + i];
                final float[] afloat4 = colRgb[i1];

                for (int j1 = 0; j1 < 3; ++j1) {
                    afloat4[j1] = afloat3[j1];
                }
            }
        } else {
            final float f = 1.0F - (x - (float) i);
            final float f1 = 1.0F - ((float) j - x);

            for (int k = 0; k < 16; ++k) {
                final float[] afloat = origMap[offset + k * width + i];
                final float[] afloat1 = origMap[offset + k * width + j];
                final float[] afloat2 = colRgb[k];

                for (int l = 0; l < 3; ++l) {
                    afloat2[l] = afloat[l] * f + afloat1[l] * f1;
                }
            }
        }
    }

    private static void dbg(final String str) {
        Config.dbg("CustomColors: " + str);
    }

    private static void warn(final String str) {
        Config.warn("CustomColors: " + str);
    }
}
