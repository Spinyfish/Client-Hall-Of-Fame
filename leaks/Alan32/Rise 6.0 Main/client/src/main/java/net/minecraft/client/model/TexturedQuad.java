package net.minecraft.client.model;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.src.Config;
import net.minecraft.util.Vec3;
import net.optifine.shaders.SVertexFormat;

public class TexturedQuad {
    public PositionTextureVertex[] vertexPositions;
    public int nVertices;
    private boolean invertNormal;

    public TexturedQuad(final PositionTextureVertex[] vertices) {
        this.vertexPositions = vertices;
        this.nVertices = vertices.length;
    }

    public TexturedQuad(final PositionTextureVertex[] vertices, final int texcoordU1, final int texcoordV1, final int texcoordU2, final int texcoordV2, final float textureWidth, final float textureHeight) {
        this(vertices);
        final float f = 0.0F / textureWidth;
        final float f1 = 0.0F / textureHeight;
        vertices[0] = vertices[0].setTexturePosition((float) texcoordU2 / textureWidth - f, (float) texcoordV1 / textureHeight + f1);
        vertices[1] = vertices[1].setTexturePosition((float) texcoordU1 / textureWidth + f, (float) texcoordV1 / textureHeight + f1);
        vertices[2] = vertices[2].setTexturePosition((float) texcoordU1 / textureWidth + f, (float) texcoordV2 / textureHeight - f1);
        vertices[3] = vertices[3].setTexturePosition((float) texcoordU2 / textureWidth - f, (float) texcoordV2 / textureHeight - f1);
    }

    public void flipFace() {
        final PositionTextureVertex[] apositiontexturevertex = new PositionTextureVertex[this.vertexPositions.length];

        for (int i = 0; i < this.vertexPositions.length; ++i) {
            apositiontexturevertex[i] = this.vertexPositions[this.vertexPositions.length - i - 1];
        }

        this.vertexPositions = apositiontexturevertex;
    }

    /**
     * Draw this primitve. This is typically called only once as the generated drawing instructions are saved by the
     * renderer and reused later.
     *
     * @param renderer The renderer instance
     * @param scale    The amount of scale to apply to this object
     */
    public void draw(final WorldRenderer renderer, final float scale) {
        final Vec3 vec3 = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[0].vector3D);
        final Vec3 vec31 = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[2].vector3D);
        final Vec3 vec32 = vec31.crossProduct(vec3).normalize();
        float f = (float) vec32.xCoord;
        float f1 = (float) vec32.yCoord;
        float f2 = (float) vec32.zCoord;

        if (this.invertNormal) {
            f = -f;
            f1 = -f1;
            f2 = -f2;
        }

        if (Config.isShaders()) {
            renderer.begin(7, SVertexFormat.defVertexFormatTextured);
        } else {
            renderer.begin(7, DefaultVertexFormats.field_181703_c);
        }

        for (int i = 0; i < 4; ++i) {
            final PositionTextureVertex positiontexturevertex = this.vertexPositions[i];
            renderer.pos(positiontexturevertex.vector3D.xCoord * (double) scale, positiontexturevertex.vector3D.yCoord * (double) scale, positiontexturevertex.vector3D.zCoord * (double) scale).tex(positiontexturevertex.texturePositionX, positiontexturevertex.texturePositionY).func_181663_c(f, f1, f2).endVertex();
        }

        Tessellator.getInstance().draw();
    }
}
