/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelGhast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.util.ResourceLocation;

public class RenderGhast
extends RenderLiving<EntityGhast> {
    private static final ResourceLocation ghastTextures = new ResourceLocation("textures/entity/ghast/ghast.png");
    private static final ResourceLocation ghastShootingTextures = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");

    public RenderGhast(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelGhast(), 0.5f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGhast entity) {
        return entity.isAttacking() ? ghastShootingTextures : ghastTextures;
    }

    @Override
    protected void preRenderCallback(EntityGhast entitylivingbaseIn, float partialTickTime) {
        float f = 1.0f;
        float f1 = (8.0f + f) / 2.0f;
        float f2 = (8.0f + 1.0f / f) / 2.0f;
        GlStateManager.scale(f2, f1, f2);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }
}

