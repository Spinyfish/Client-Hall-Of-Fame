package net.optifine;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.src.Config;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import java.util.IdentityHashMap;
import java.util.Map;

public class NaturalProperties {
    public int rotation = 1;
    public boolean flip = false;
    private final Map[] quadMaps = new Map[8];

    public NaturalProperties(final String type) {
        switch (type) {
            case "4":
                this.rotation = 4;
                break;
            case "2":
                this.rotation = 2;
                break;
            case "F":
                this.flip = true;
                break;
            case "4F":
                this.rotation = 4;
                this.flip = true;
                break;
            case "2F":
                this.rotation = 2;
                this.flip = true;
                break;
            default:
                Config.warn("NaturalTextures: Unknown type: " + type);
                break;
        }
    }

    public boolean isValid() {
        return this.rotation == 2 || this.rotation == 4 || this.flip;
    }

    public synchronized BakedQuad getQuad(final BakedQuad quadIn, final int rotate, final boolean flipU) {
        int i = rotate;

        if (flipU) {
            i = rotate | 4;
        }

        if (i > 0 && i < this.quadMaps.length) {
            Map map = this.quadMaps[i];

            if (map == null) {
                map = new IdentityHashMap(1);
                this.quadMaps[i] = map;
            }

            BakedQuad bakedquad = (BakedQuad) map.get(quadIn);

            if (bakedquad == null) {
                bakedquad = this.makeQuad(quadIn, rotate, flipU);
                map.put(quadIn, bakedquad);
            }

            return bakedquad;
        } else {
            return quadIn;
        }
    }

    private BakedQuad makeQuad(final BakedQuad quad, int rotate, final boolean flipU) {
        int[] aint = quad.getVertexData();
        final int i = quad.getTintIndex();
        final EnumFacing enumfacing = quad.getFace();
        final TextureAtlasSprite textureatlassprite = quad.getSprite();

        if (!this.isFullSprite(quad)) {
            rotate = 0;
        }

        aint = this.transformVertexData(aint, rotate, flipU);
        final BakedQuad bakedquad = new BakedQuad(aint, i, enumfacing, textureatlassprite);
        return bakedquad;
    }

    private int[] transformVertexData(final int[] vertexData, final int rotate, final boolean flipU) {
        final int[] aint = vertexData.clone();
        int i = 4 - rotate;

        if (flipU) {
            i += 3;
        }

        i = i % 4;
        final int j = aint.length / 4;

        for (int k = 0; k < 4; ++k) {
            final int l = k * j;
            final int i1 = i * j;
            aint[i1 + 4] = vertexData[l + 4];
            aint[i1 + 4 + 1] = vertexData[l + 4 + 1];

            if (flipU) {
                --i;

                if (i < 0) {
                    i = 3;
                }
            } else {
                ++i;

                if (i > 3) {
                    i = 0;
                }
            }
        }

        return aint;
    }

    private boolean isFullSprite(final BakedQuad quad) {
        final TextureAtlasSprite textureatlassprite = quad.getSprite();
        final float f = textureatlassprite.getMinU();
        final float f1 = textureatlassprite.getMaxU();
        final float f2 = f1 - f;
        final float f3 = f2 / 256.0F;
        final float f4 = textureatlassprite.getMinV();
        final float f5 = textureatlassprite.getMaxV();
        final float f6 = f5 - f4;
        final float f7 = f6 / 256.0F;
        final int[] aint = quad.getVertexData();
        final int i = aint.length / 4;

        for (int j = 0; j < 4; ++j) {
            final int k = j * i;
            final float f8 = Float.intBitsToFloat(aint[k + 4]);
            final float f9 = Float.intBitsToFloat(aint[k + 4 + 1]);

            if (!this.equalsDelta(f8, f, f3) && !this.equalsDelta(f8, f1, f3)) {
                return false;
            }

            if (!this.equalsDelta(f9, f4, f7) && !this.equalsDelta(f9, f5, f7)) {
                return false;
            }
        }

        return true;
    }

    private boolean equalsDelta(final float x1, final float x2, final float deltaMax) {
        final float f = MathHelper.abs(x1 - x2);
        return f < deltaMax;
    }
}
