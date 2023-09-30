/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderArrow
extends Render<EntityArrow> {
    private static final ResourceLocation arrowTextures = new ResourceLocation("textures/entity/arrow.png");

    public RenderArrow(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public void doRender(EntityArrow entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.bindEntityTexture(entity);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0f, 0.0f, 1.0f);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        int i = 0;
        float f = 0.0f;
        float f1 = 0.5f;
        float f2 = (float)(0 + i * 10) / 32.0f;
        float f3 = (float)(5 + i * 10) / 32.0f;
        float f4 = 0.0f;
        float f5 = 0.15625f;
        float f6 = (float)(5 + i * 10) / 32.0f;
        float f7 = (float)(10 + i * 10) / 32.0f;
        float f8 = 0.05625f;
        GlStateManager.enableRescaleNormal();
        float f9 = (float)entity.arrowShake - partialTicks;
        if (f9 > 0.0f) {
            float f10 = -MathHelper.sin(f9 * 3.0f) * f9;
            GlStateManager.rotate(f10, 0.0f, 0.0f, 1.0f);
        }
        GlStateManager.rotate(45.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(f8, f8, f8);
        GlStateManager.translate(-4.0f, 0.0f, 0.0f);
        GL11.glNormal3f((float)f8, (float)0.0f, (float)0.0f);
        worldrenderer.begin(7, DefaultVertexFormats.field_181707_g);
        worldrenderer.pos(-7.0, -2.0, -2.0).func_181673_a(f4, f6).end();
        worldrenderer.pos(-7.0, -2.0, 2.0).func_181673_a(f5, f6).end();
        worldrenderer.pos(-7.0, 2.0, 2.0).func_181673_a(f5, f7).end();
        worldrenderer.pos(-7.0, 2.0, -2.0).func_181673_a(f4, f7).end();
        tessellator.draw();
        GL11.glNormal3f((float)(-f8), (float)0.0f, (float)0.0f);
        worldrenderer.begin(7, DefaultVertexFormats.field_181707_g);
        worldrenderer.pos(-7.0, 2.0, -2.0).func_181673_a(f4, f6).end();
        worldrenderer.pos(-7.0, 2.0, 2.0).func_181673_a(f5, f6).end();
        worldrenderer.pos(-7.0, -2.0, 2.0).func_181673_a(f5, f7).end();
        worldrenderer.pos(-7.0, -2.0, -2.0).func_181673_a(f4, f7).end();
        tessellator.draw();
        for (int j = 0; j < 4; ++j) {
            GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
            GL11.glNormal3f((float)0.0f, (float)0.0f, (float)f8);
            worldrenderer.begin(7, DefaultVertexFormats.field_181707_g);
            worldrenderer.pos(-8.0, -2.0, 0.0).func_181673_a(f, f2).end();
            worldrenderer.pos(8.0, -2.0, 0.0).func_181673_a(f1, f2).end();
            worldrenderer.pos(8.0, 2.0, 0.0).func_181673_a(f1, f3).end();
            worldrenderer.pos(-8.0, 2.0, 0.0).func_181673_a(f, f3).end();
            tessellator.draw();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityArrow entity) {
        return arrowTextures;
    }
}

