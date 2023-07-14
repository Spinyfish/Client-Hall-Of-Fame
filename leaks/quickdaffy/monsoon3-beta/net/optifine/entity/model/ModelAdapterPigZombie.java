/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPigZombie;
import net.minecraft.entity.monster.EntityPigZombie;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterBiped;

public class ModelAdapterPigZombie
extends ModelAdapterBiped {
    public ModelAdapterPigZombie() {
        super(EntityPigZombie.class, "zombie_pigman", 0.5f);
    }

    @Override
    public ModelBase makeModel() {
        return new ModelZombie();
    }

    @Override
    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderPigZombie renderpigzombie = new RenderPigZombie(rendermanager);
        Render.setModelBipedMain(renderpigzombie, (ModelBiped)modelBase);
        renderpigzombie.mainModel = modelBase;
        renderpigzombie.shadowSize = shadowSize;
        return renderpigzombie;
    }
}

