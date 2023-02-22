package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderCaveSpider;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityCaveSpider;

public class ModelAdapterCaveSpider extends ModelAdapterSpider {
    public ModelAdapterCaveSpider() {
        super(EntityCaveSpider.class, "cave_spider", 0.7F);
    }

    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderCaveSpider rendercavespider = new RenderCaveSpider(rendermanager);
        rendercavespider.mainModel = modelBase;
        rendercavespider.shadowSize = shadowSize;
        return rendercavespider;
    }
}
