/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerIronGolemFlower;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;

public class RenderIronGolem
extends RenderLiving<EntityIronGolem> {
    private static final ResourceLocation ironGolemTextures = new ResourceLocation("textures/entity/iron_golem.png");

    public RenderIronGolem(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelIronGolem(), 0.5f);
        this.addLayer(new LayerIronGolemFlower(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityIronGolem entity) {
        return ironGolemTextures;
    }

    @Override
    protected void rotateCorpse(EntityIronGolem bat, float p_77043_2_, float p_77043_3_, float partialTicks) {
        super.rotateCorpse(bat, p_77043_2_, p_77043_3_, partialTicks);
        if ((double)bat.limbSwingAmount >= 0.01) {
            float f = 13.0f;
            float f1 = bat.limbSwing - bat.limbSwingAmount * (1.0f - partialTicks) + 6.0f;
            float f2 = (Math.abs(f1 % f - f * 0.5f) - f * 0.25f) / (f * 0.25f);
            GlStateManager.rotate(6.5f * f2, 0.0f, 0.0f, 1.0f);
        }
    }
}

