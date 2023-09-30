/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.src.Config;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterMinecart
extends ModelAdapter {
    public ModelAdapterMinecart() {
        super(EntityMinecart.class, "minecart", 0.5f);
    }

    protected ModelAdapterMinecart(Class entityClass, String name, float shadow) {
        super(entityClass, name, shadow);
    }

    @Override
    public ModelBase makeModel() {
        return new ModelMinecart();
    }

    @Override
    public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
        if (!(model instanceof ModelMinecart)) {
            return null;
        }
        ModelMinecart modelminecart = (ModelMinecart)model;
        return modelPart.equals("bottom") ? modelminecart.sideModels[0] : (modelPart.equals("back") ? modelminecart.sideModels[1] : (modelPart.equals("front") ? modelminecart.sideModels[2] : (modelPart.equals("right") ? modelminecart.sideModels[3] : (modelPart.equals("left") ? modelminecart.sideModels[4] : (modelPart.equals("dirt") ? modelminecart.sideModels[5] : null)))));
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"bottom", "back", "front", "right", "left", "dirt"};
    }

    @Override
    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderMinecart renderminecart = new RenderMinecart(rendermanager);
        if (!Reflector.RenderMinecart_modelMinecart.exists()) {
            Config.warn("Field not found: RenderMinecart.modelMinecart");
            return null;
        }
        Reflector.setFieldValue(renderminecart, Reflector.RenderMinecart_modelMinecart, modelBase);
        renderminecart.shadowSize = shadowSize;
        return renderminecart;
    }
}

