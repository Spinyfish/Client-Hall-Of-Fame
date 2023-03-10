/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.util.ResourceLocation;

public class RenderMagmaCube
extends RenderLiving<EntityMagmaCube> {
    private static final ResourceLocation magmaCubeTextures = new ResourceLocation("textures/entity/slime/magmacube.png");

    public RenderMagmaCube(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelMagmaCube(), 0.25f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMagmaCube entity) {
        return magmaCubeTextures;
    }

    @Override
    protected void preRenderCallback(EntityMagmaCube entitylivingbaseIn, float partialTickTime) {
        int i = entitylivingbaseIn.getSlimeSize();
        float f = (entitylivingbaseIn.prevSquishFactor + (entitylivingbaseIn.squishFactor - entitylivingbaseIn.prevSquishFactor) * partialTickTime) / ((float)i * 0.5f + 1.0f);
        float f1 = 1.0f / (f + 1.0f);
        float f2 = i;
        GlStateManager.scale(f1 * f2, 1.0f / f1 * f2, f1 * f2);
    }
}

