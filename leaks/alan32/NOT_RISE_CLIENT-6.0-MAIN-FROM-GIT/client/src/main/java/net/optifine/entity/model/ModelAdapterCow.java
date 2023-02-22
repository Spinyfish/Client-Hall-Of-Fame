package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.renderer.entity.RenderCow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityCow;

public class ModelAdapterCow extends ModelAdapterQuadruped {
    public ModelAdapterCow() {
        super(EntityCow.class, "cow", 0.7F);
    }

    public ModelBase makeModel() {
        return new ModelCow();
    }

    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        return new RenderCow(rendermanager, modelBase, shadowSize);
    }
}
