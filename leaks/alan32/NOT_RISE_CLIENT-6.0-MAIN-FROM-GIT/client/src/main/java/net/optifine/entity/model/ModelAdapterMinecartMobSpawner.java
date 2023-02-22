package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecartMobSpawner;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterMinecartMobSpawner extends ModelAdapterMinecart {
    public ModelAdapterMinecartMobSpawner() {
        super(EntityMinecartMobSpawner.class, "spawner_minecart", 0.5F);
    }

    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderMinecartMobSpawner renderminecartmobspawner = new RenderMinecartMobSpawner(rendermanager);

        if (!Reflector.RenderMinecart_modelMinecart.exists()) {
            Config.warn("Field not found: RenderMinecart.modelMinecart");
            return null;
        } else {
            Reflector.setFieldValue(renderminecartmobspawner, Reflector.RenderMinecart_modelMinecart, modelBase);
            renderminecartmobspawner.shadowSize = shadowSize;
            return renderminecartmobspawner;
        }
    }
}
