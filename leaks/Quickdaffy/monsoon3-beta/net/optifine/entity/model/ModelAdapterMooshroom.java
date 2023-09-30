/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMooshroom;
import net.minecraft.entity.passive.EntityMooshroom;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterQuadruped;

public class ModelAdapterMooshroom
extends ModelAdapterQuadruped {
    public ModelAdapterMooshroom() {
        super(EntityMooshroom.class, "mooshroom", 0.7f);
    }

    @Override
    public ModelBase makeModel() {
        return new ModelCow();
    }

    @Override
    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderMooshroom rendermooshroom = new RenderMooshroom(rendermanager, modelBase, shadowSize);
        return rendermooshroom;
    }
}

