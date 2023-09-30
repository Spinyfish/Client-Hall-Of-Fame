/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerSlimeGel;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;

public class RenderSlime
extends RenderLiving<EntitySlime> {
    private static final ResourceLocation slimeTextures = new ResourceLocation("textures/entity/slime/slime.png");

    public RenderSlime(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
        this.addLayer(new LayerSlimeGel(this));
    }

    @Override
    public void doRender(EntitySlime entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.shadowSize = 0.25f * (float)entity.getSlimeSize();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected void preRenderCallback(EntitySlime entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSlimeSize();
        float f1 = (entitylivingbaseIn.prevSquishFactor + (entitylivingbaseIn.squishFactor - entitylivingbaseIn.prevSquishFactor) * partialTickTime) / (f * 0.5f + 1.0f);
        float f2 = 1.0f / (f1 + 1.0f);
        GlStateManager.scale(f2 * f, 1.0f / f2 * f, f2 * f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySlime entity) {
        return slimeTextures;
    }
}

