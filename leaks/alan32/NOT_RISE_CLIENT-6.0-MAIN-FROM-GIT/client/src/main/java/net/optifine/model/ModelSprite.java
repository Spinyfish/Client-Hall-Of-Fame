package net.optifine.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelSprite {
    private ModelRenderer modelRenderer = null;
    private int textureOffsetX = 0;
    private int textureOffsetY = 0;
    private float posX = 0.0F;
    private float posY = 0.0F;
    private float posZ = 0.0F;
    private int sizeX = 0;
    private int sizeY = 0;
    private int sizeZ = 0;
    private float sizeAdd = 0.0F;
    private float minU = 0.0F;
    private float minV = 0.0F;
    private float maxU = 0.0F;
    private float maxV = 0.0F;

    public ModelSprite(final ModelRenderer modelRenderer, final int textureOffsetX, final int textureOffsetY, final float posX, final float posY, final float posZ, final int sizeX, final int sizeY, final int sizeZ, final float sizeAdd) {
        this.modelRenderer = modelRenderer;
        this.textureOffsetX = textureOffsetX;
        this.textureOffsetY = textureOffsetY;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.sizeAdd = sizeAdd;
        this.minU = (float) textureOffsetX / modelRenderer.textureWidth;
        this.minV = (float) textureOffsetY / modelRenderer.textureHeight;
        this.maxU = (float) (textureOffsetX + sizeX) / modelRenderer.textureWidth;
        this.maxV = (float) (textureOffsetY + sizeY) / modelRenderer.textureHeight;
    }

    public void render(final Tessellator tessellator, final float scale) {
        GlStateManager.translate(this.posX * scale, this.posY * scale, this.posZ * scale);
        float f = this.minU;
        float f1 = this.maxU;
        float f2 = this.minV;
        float f3 = this.maxV;

        if (this.modelRenderer.mirror) {
            f = this.maxU;
            f1 = this.minU;
        }

        if (this.modelRenderer.mirrorV) {
            f2 = this.maxV;
            f3 = this.minV;
        }

        renderItemIn2D(tessellator, f, f2, f1, f3, this.sizeX, this.sizeY, scale * (float) this.sizeZ, this.modelRenderer.textureWidth, this.modelRenderer.textureHeight);
        GlStateManager.translate(-this.posX * scale, -this.posY * scale, -this.posZ * scale);
    }

    public static void renderItemIn2D(final Tessellator tess, final float minU, final float minV, final float maxU, final float maxV, final int sizeX, final int sizeY, float width, final float texWidth, final float texHeight) {
        if (width < 6.25E-4F) {
            width = 6.25E-4F;
        }

        final float f = maxU - minU;
        final float f1 = maxV - minV;
        final double d0 = MathHelper.abs(f) * (texWidth / 16.0F);
        final double d1 = MathHelper.abs(f1) * (texHeight / 16.0F);
        final WorldRenderer worldrenderer = tess.getWorldRenderer();
        GL11.glNormal3f(0.0F, 0.0F, -1.0F);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0.0D, d1, 0.0D).tex(minU, maxV).endVertex();
        worldrenderer.pos(d0, d1, 0.0D).tex(maxU, maxV).endVertex();
        worldrenderer.pos(d0, 0.0D, 0.0D).tex(maxU, minV).endVertex();
        worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(minU, minV).endVertex();
        tess.draw();
        GL11.glNormal3f(0.0F, 0.0F, 1.0F);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0.0D, 0.0D, width).tex(minU, minV).endVertex();
        worldrenderer.pos(d0, 0.0D, width).tex(maxU, minV).endVertex();
        worldrenderer.pos(d0, d1, width).tex(maxU, maxV).endVertex();
        worldrenderer.pos(0.0D, d1, width).tex(minU, maxV).endVertex();
        tess.draw();
        final float f2 = 0.5F * f / (float) sizeX;
        final float f3 = 0.5F * f1 / (float) sizeY;
        GL11.glNormal3f(-1.0F, 0.0F, 0.0F);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

        for (int i = 0; i < sizeX; ++i) {
            final float f4 = (float) i / (float) sizeX;
            final float f5 = minU + f * f4 + f2;
            worldrenderer.pos((double) f4 * d0, d1, width).tex(f5, maxV).endVertex();
            worldrenderer.pos((double) f4 * d0, d1, 0.0D).tex(f5, maxV).endVertex();
            worldrenderer.pos((double) f4 * d0, 0.0D, 0.0D).tex(f5, minV).endVertex();
            worldrenderer.pos((double) f4 * d0, 0.0D, width).tex(f5, minV).endVertex();
        }

        tess.draw();
        GL11.glNormal3f(1.0F, 0.0F, 0.0F);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

        for (int j = 0; j < sizeX; ++j) {
            final float f7 = (float) j / (float) sizeX;
            final float f10 = minU + f * f7 + f2;
            final float f6 = f7 + 1.0F / (float) sizeX;
            worldrenderer.pos((double) f6 * d0, 0.0D, width).tex(f10, minV).endVertex();
            worldrenderer.pos((double) f6 * d0, 0.0D, 0.0D).tex(f10, minV).endVertex();
            worldrenderer.pos((double) f6 * d0, d1, 0.0D).tex(f10, maxV).endVertex();
            worldrenderer.pos((double) f6 * d0, d1, width).tex(f10, maxV).endVertex();
        }

        tess.draw();
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

        for (int k = 0; k < sizeY; ++k) {
            final float f8 = (float) k / (float) sizeY;
            final float f11 = minV + f1 * f8 + f3;
            final float f13 = f8 + 1.0F / (float) sizeY;
            worldrenderer.pos(0.0D, (double) f13 * d1, width).tex(minU, f11).endVertex();
            worldrenderer.pos(d0, (double) f13 * d1, width).tex(maxU, f11).endVertex();
            worldrenderer.pos(d0, (double) f13 * d1, 0.0D).tex(maxU, f11).endVertex();
            worldrenderer.pos(0.0D, (double) f13 * d1, 0.0D).tex(minU, f11).endVertex();
        }

        tess.draw();
        GL11.glNormal3f(0.0F, -1.0F, 0.0F);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

        for (int l = 0; l < sizeY; ++l) {
            final float f9 = (float) l / (float) sizeY;
            final float f12 = minV + f1 * f9 + f3;
            worldrenderer.pos(d0, (double) f9 * d1, width).tex(maxU, f12).endVertex();
            worldrenderer.pos(0.0D, (double) f9 * d1, width).tex(minU, f12).endVertex();
            worldrenderer.pos(0.0D, (double) f9 * d1, 0.0D).tex(minU, f12).endVertex();
            worldrenderer.pos(d0, (double) f9 * d1, 0.0D).tex(maxU, f12).endVertex();
        }

        tess.draw();
    }
}
