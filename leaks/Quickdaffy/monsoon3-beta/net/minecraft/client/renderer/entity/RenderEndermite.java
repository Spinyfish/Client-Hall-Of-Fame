/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelEnderMite;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.util.ResourceLocation;

public class RenderEndermite
extends RenderLiving<EntityEndermite> {
    private static final ResourceLocation ENDERMITE_TEXTURES = new ResourceLocation("textures/entity/endermite.png");

    public RenderEndermite(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelEnderMite(), 0.3f);
    }

    @Override
    protected float getDeathMaxRotation(EntityEndermite entityLivingBaseIn) {
        return 180.0f;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityEndermite entity) {
        return ENDERMITE_TEXTURES;
    }
}

