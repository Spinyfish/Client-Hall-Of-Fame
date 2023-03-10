/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.ResourceLocation;

public class RenderSilverfish
extends RenderLiving<EntitySilverfish> {
    private static final ResourceLocation silverfishTextures = new ResourceLocation("textures/entity/silverfish.png");

    public RenderSilverfish(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelSilverfish(), 0.3f);
    }

    @Override
    protected float getDeathMaxRotation(EntitySilverfish entityLivingBaseIn) {
        return 180.0f;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySilverfish entity) {
        return silverfishTextures;
    }
}

