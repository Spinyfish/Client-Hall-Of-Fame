package net.minecraft.client.renderer.block.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.IVertexProducer;

public class BakedQuad implements IVertexProducer
{
    /**
     * Joined 4 vertex records, each has 7 fields (x, y, z, shadeColor, u, v, <unused>), see
     * FaceBakery.storeVertexData()
     */
    protected final int[] vertexData;
    protected final int tintIndex;
    protected final EnumFacing face;
    private TextureAtlasSprite sprite = null;
    private int[] vertexDataSingle = null;

    public BakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn, TextureAtlasSprite sprite)
    {
        vertexDataIn = fixVertexData(vertexDataIn);
        this.vertexData = vertexDataIn;
        this.tintIndex = tintIndexIn;
        this.face = faceIn;
        this.sprite = sprite;
    }

    public TextureAtlasSprite getSprite()
    {
        if (this.sprite == null)
        {
            this.sprite = getSpriteByUv(this.getVertexData());
        }

        return this.sprite;
    }

    public String toString()
    {
        return "vertex: " + this.vertexData.length / 7 + ", tint: " + this.tintIndex + ", facing: " + this.face + ", sprite: " + this.sprite;
    }

    public BakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn)
    {
        vertexDataIn = fixVertexData(vertexDataIn);
        this.vertexData = vertexDataIn;
        this.tintIndex = tintIndexIn;
        this.face = faceIn;
    }

    public int[] getVertexData()
    {
        return this.vertexData;
    }

    public boolean hasTintIndex()
    {
        return this.tintIndex != -1;
    }

    public int getTintIndex()
    {
        return this.tintIndex;
    }

    public EnumFacing getFace()
    {
        return this.face;
    }

    public int[] getVertexDataSingle()
    {
        if (this.vertexDataSingle == null)
        {
            this.vertexDataSingle = makeVertexDataSingle(this.getVertexData(), this.getSprite());
        }

        return this.vertexDataSingle;
    }

    private static int[] makeVertexDataSingle(int[] vd, TextureAtlasSprite sprite)
    {
        int[] vdSingle = (int[])vd.clone();
        int ku = sprite.sheetWidth / sprite.getIconWidth();
        int kv = sprite.sheetHeight / sprite.getIconHeight();
        int step = vdSingle.length / 4;

        for (int i = 0; i < 4; ++i)
        {
            int pos = i * step;
            float tu = Float.intBitsToFloat(vdSingle[pos + 4]);
            float tv = Float.intBitsToFloat(vdSingle[pos + 4 + 1]);
            float u = sprite.toSingleU(tu);
            float v = sprite.toSingleV(tv);
            vdSingle[pos + 4] = Float.floatToRawIntBits(u);
            vdSingle[pos + 4 + 1] = Float.floatToRawIntBits(v);
        }

        return vdSingle;
    }

    public void pipe(IVertexConsumer consumer)
    {
        Reflector.callVoid(Reflector.LightUtil_putBakedQuad, new Object[] {consumer, this});
    }

    private static TextureAtlasSprite getSpriteByUv(int[] vertexData)
    {
        float uMin = 1.0F;
        float vMin = 1.0F;
        float uMax = 0.0F;
        float vMax = 0.0F;
        int step = vertexData.length / 4;

        for (int uMid = 0; uMid < 4; ++uMid)
        {
            int vMid = uMid * step;
            float spriteUv = Float.intBitsToFloat(vertexData[vMid + 4]);
            float tv = Float.intBitsToFloat(vertexData[vMid + 4 + 1]);
            uMin = Math.min(uMin, spriteUv);
            vMin = Math.min(vMin, tv);
            uMax = Math.max(uMax, spriteUv);
            vMax = Math.max(vMax, tv);
        }

        float var10 = (uMin + uMax) / 2.0F;
        float var11 = (vMin + vMax) / 2.0F;
        TextureAtlasSprite var12 = Minecraft.getMinecraft().getTextureMapBlocks().getIconByUV((double)var10, (double)var11);
        return var12;
    }

    private static int[] fixVertexData(int[] vd)
    {
        if (Config.isShaders())
        {
            if (vd.length == 28)
            {
                vd = expandVertexData(vd);
            }
        }
        else if (vd.length == 56)
        {
            vd = compactVertexData(vd);
        }

        return vd;
    }

    private static int[] expandVertexData(int[] vd)
    {
        int step = vd.length / 4;
        int stepNew = step * 2;
        int[] vdNew = new int[stepNew * 4];

        for (int i = 0; i < 4; ++i)
        {
            System.arraycopy(vd, i * step, vdNew, i * stepNew, step);
        }

        return vdNew;
    }

    private static int[] compactVertexData(int[] vd)
    {
        int step = vd.length / 4;
        int stepNew = step / 2;
        int[] vdNew = new int[stepNew * 4];

        for (int i = 0; i < 4; ++i)
        {
            System.arraycopy(vd, i * step, vdNew, i * stepNew, stepNew);
        }

        return vdNew;
    }
}
