/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.PositionTextureVertex;
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

    public TexturedQuad(PositionTextureVertex[] vertices) {
        this.vertexPositions = vertices;
        this.nVertices = vertices.length;
    }

    public TexturedQuad(PositionTextureVertex[] vertices, int texcoordU1, int texcoordV1, int texcoordU2, int texcoordV2, float textureWidth, float textureHeight) {
        this(vertices);
        float f = 0.0f / textureWidth;
        float f1 = 0.0f / textureHeight;
        vertices[0] = vertices[0].setTexturePosition((float)texcoordU2 / textureWidth - f, (float)texcoordV1 / textureHeight + f1);
        vertices[1] = vertices[1].setTexturePosition((float)texcoordU1 / textureWidth + f, (float)texcoordV1 / textureHeight + f1);
        vertices[2] = vertices[2].setTexturePosition((float)texcoordU1 / textureWidth + f, (float)texcoordV2 / textureHeight - f1);
        vertices[3] = vertices[3].setTexturePosition((float)texcoordU2 / textureWidth - f, (float)texcoordV2 / textureHeight - f1);
    }

    public void flipFace() {
        PositionTextureVertex[] apositiontexturevertex = new PositionTextureVertex[this.vertexPositions.length];
        for (int i = 0; i < this.vertexPositions.length; ++i) {
            apositiontexturevertex[i] = this.vertexPositions[this.vertexPositions.length - i - 1];
        }
        this.vertexPositions = apositiontexturevertex;
    }

    public void draw(WorldRenderer renderer, float scale) {
        Vec3 vec3 = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[0].vector3D);
        Vec3 vec31 = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[2].vector3D);
        Vec3 vec32 = vec31.crossProduct(vec3).normalize();
        float f = (float)vec32.x;
        float f1 = (float)vec32.y;
        float f2 = (float)vec32.z;
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
            PositionTextureVertex positiontexturevertex = this.vertexPositions[i];
            renderer.pos(positiontexturevertex.vector3D.x * (double)scale, positiontexturevertex.vector3D.y * (double)scale, positiontexturevertex.vector3D.z * (double)scale).func_181673_a(positiontexturevertex.texturePositionX, positiontexturevertex.texturePositionY).func_181663_c(f, f1, f2).end();
        }
        Tessellator.getInstance().draw();
    }
}

