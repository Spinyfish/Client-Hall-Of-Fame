/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelSnowMan;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerSnowmanHead;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.util.ResourceLocation;

public class RenderSnowMan
extends RenderLiving<EntitySnowman> {
    private static final ResourceLocation snowManTextures = new ResourceLocation("textures/entity/snowman.png");

    public RenderSnowMan(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelSnowMan(), 0.5f);
        this.addLayer(new LayerSnowmanHead(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySnowman entity) {
        return snowManTextures;
    }

    @Override
    public ModelSnowMan getMainModel() {
        return (ModelSnowMan)super.getMainModel();
    }
}

