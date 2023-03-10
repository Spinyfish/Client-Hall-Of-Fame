/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.ResourceLocation;

public class RenderOcelot
extends RenderLiving<EntityOcelot> {
    private static final ResourceLocation blackOcelotTextures = new ResourceLocation("textures/entity/cat/black.png");
    private static final ResourceLocation ocelotTextures = new ResourceLocation("textures/entity/cat/ocelot.png");
    private static final ResourceLocation redOcelotTextures = new ResourceLocation("textures/entity/cat/red.png");
    private static final ResourceLocation siameseOcelotTextures = new ResourceLocation("textures/entity/cat/siamese.png");

    public RenderOcelot(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityOcelot entity) {
        switch (entity.getTameSkin()) {
            default: {
                return ocelotTextures;
            }
            case 1: {
                return blackOcelotTextures;
            }
            case 2: {
                return redOcelotTextures;
            }
            case 3: 
        }
        return siameseOcelotTextures;
    }

    @Override
    protected void preRenderCallback(EntityOcelot entitylivingbaseIn, float partialTickTime) {
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
        if (entitylivingbaseIn.isTamed()) {
            GlStateManager.scale(0.8f, 0.8f, 0.8f);
        }
    }
}

