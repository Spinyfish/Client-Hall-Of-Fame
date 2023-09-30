/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerSpiderEyes;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;

public class RenderSpider<T extends EntitySpider>
extends RenderLiving<T> {
    private static final ResourceLocation spiderTextures = new ResourceLocation("textures/entity/spider/spider.png");

    public RenderSpider(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelSpider(), 1.0f);
        this.addLayer(new LayerSpiderEyes(this));
    }

    @Override
    protected float getDeathMaxRotation(T entityLivingBaseIn) {
        return 180.0f;
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return spiderTextures;
    }
}

