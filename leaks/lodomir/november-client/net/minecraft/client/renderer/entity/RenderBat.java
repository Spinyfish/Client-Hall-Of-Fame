/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderBat
extends RenderLiving<EntityBat> {
    private static final ResourceLocation batTextures = new ResourceLocation("textures/entity/bat.png");

    public RenderBat(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelBat(), 0.25f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBat entity) {
        return batTextures;
    }

    @Override
    protected void preRenderCallback(EntityBat entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(0.35f, 0.35f, 0.35f);
    }

    @Override
    protected void rotateCorpse(EntityBat bat, float p_77043_2_, float p_77043_3_, float partialTicks) {
        if (!bat.getIsBatHanging()) {
            GlStateManager.translate(0.0f, MathHelper.cos(p_77043_2_ * 0.3f) * 0.1f, 0.0f);
        } else {
            GlStateManager.translate(0.0f, -0.1f, 0.0f);
        }
        super.rotateCorpse(bat, p_77043_2_, p_77043_3_, partialTicks);
    }
}

