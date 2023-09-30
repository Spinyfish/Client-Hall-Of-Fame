package net.minecraft.client.model;

import net.minecraft.client.renderer.WorldRenderer;

public class ModelBox
{
    /**
     * The (x,y,z) vertex positions and (u,v) texture coordinates for each of the 8 points on a cube
     */
    private PositionTextureVertex[] vertexPositions;

    /** An array of 6 TexturedQuads, one for each face of a cube */
    private TexturedQuad[] quadList;

    /** X vertex coordinate of lower box corner */
    public final float posX1;

    /** Y vertex coordinate of lower box corner */
    public final float posY1;

    /** Z vertex coordinate of lower box corner */
    public final float posZ1;

    /** X vertex coordinate of upper box corner */
    public final float posX2;

    /** Y vertex coordinate of upper box corner */
    public final float posY2;

    /** Z vertex coordinate of upper box corner */
    public final float posZ2;
    public String boxName;

    public ModelBox(ModelRenderer renderer, int textureX, int textureY, float posX1, float posY1, float posZ1, int posX2, int posY2, int posZ2, float scaleFactor)
    {
        this(renderer, textureX, textureY, posX1, posY1, posZ1, posX2, posY2, posZ2, scaleFactor, renderer.mirror);
    }

    public ModelBox(ModelRenderer renderer, int textureX, int textureY, float posX1, float posY1, float posZ1, int posX2, int posY2, int posZ2, float scaleFactor, boolean p_i46301_11_)
    {
        this.posX1 = posX1;
        this.posY1 = posY1;
        this.posZ1 = posZ1;
        this.posX2 = posX1 + (float)posX2;
        this.posY2 = posY1 + (float)posY2;
        this.posZ2 = posZ1 + (float)posZ2;
        this.vertexPositions = new PositionTextureVertex[8];
        this.quadList = new TexturedQuad[6];
        float f = posX1 + (float)posX2;
        float f1 = posY1 + (float)posY2;
        float f2 = posZ1 + (float)posZ2;
        posX1 = posX1 - scaleFactor;
        posY1 = posY1 - scaleFactor;
        posZ1 = posZ1 - scaleFactor;
        f = f + scaleFactor;
        f1 = f1 + scaleFactor;
        f2 = f2 + scaleFactor;

        if (p_i46301_11_)
        {
            float f3 = f;
            f = posX1;
            posX1 = f3;
        }

        PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(posX1, posY1, posZ1, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex = new PositionTextureVertex(f, posY1, posZ1, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f, f1, posZ1, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(posX1, f1, posZ1, 8.0F, 0.0F);
        PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(posX1, posY1, f2, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(f, posY1, f2, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(posX1, f1, f2, 8.0F, 0.0F);
        this.vertexPositions[0] = positiontexturevertex7;
        this.vertexPositions[1] = positiontexturevertex;
        this.vertexPositions[2] = positiontexturevertex1;
        this.vertexPositions[3] = positiontexturevertex2;
        this.vertexPositions[4] = positiontexturevertex3;
        this.vertexPositions[5] = positiontexturevertex4;
        this.vertexPositions[6] = positiontexturevertex5;
        this.vertexPositions[7] = positiontexturevertex6;
        this.quadList[0] = new TexturedQuad(new PositionTextureVertex[] {positiontexturevertex4, positiontexturevertex, positiontexturevertex1, positiontexturevertex5}, textureX + posZ2 + posX2, textureY + posZ2, textureX + posZ2 + posX2 + posZ2, textureY + posZ2 + posY2, renderer.textureWidth, renderer.textureHeight);
        this.quadList[1] = new TexturedQuad(new PositionTextureVertex[] {positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2}, textureX, textureY + posZ2, textureX + posZ2, textureY + posZ2 + posY2, renderer.textureWidth, renderer.textureHeight);
        this.quadList[2] = new TexturedQuad(new PositionTextureVertex[] {positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex}, textureX + posZ2, textureY, textureX + posZ2 + posX2, textureY + posZ2, renderer.textureWidth, renderer.textureHeight);
        this.quadList[3] = new TexturedQuad(new PositionTextureVertex[] {positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5}, textureX + posZ2 + posX2, textureY + posZ2, textureX + posZ2 + posX2 + posX2, textureY, renderer.textureWidth, renderer.textureHeight);
        this.quadList[4] = new TexturedQuad(new PositionTextureVertex[] {positiontexturevertex, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1}, textureX + posZ2, textureY + posZ2, textureX + posZ2 + posX2, textureY + posZ2 + posY2, renderer.textureWidth, renderer.textureHeight);
        this.quadList[5] = new TexturedQuad(new PositionTextureVertex[] {positiontexturevertex3, positiontexturevertex4, positiontexturevertex5, positiontexturevertex6}, textureX + posZ2 + posX2 + posZ2, textureY + posZ2, textureX + posZ2 + posX2 + posZ2 + posX2, textureY + posZ2 + posY2, renderer.textureWidth, renderer.textureHeight);

        if (p_i46301_11_)
        {
            for (int i = 0; i < this.quadList.length; ++i)
            {
                this.quadList[i].flipFace();
            }
        }
    }

    public void render(WorldRenderer renderer, float scale)
    {
        for (int i = 0; i < this.quadList.length; ++i)
        {
            this.quadList[i].draw(renderer, scale);
        }
    }

    public ModelBox setBoxName(String name)
    {
        this.boxName = name;
        return this;
    }
}
