/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.src.Config;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterBoat
extends ModelAdapter {
    public ModelAdapterBoat() {
        super(EntityBoat.class, "boat", 0.5f);
    }

    @Override
    public ModelBase makeModel() {
        return new ModelBoat();
    }

    @Override
    public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
        if (!(model instanceof ModelBoat)) {
            return null;
        }
        ModelBoat modelboat = (ModelBoat)model;
        return modelPart.equals("bottom") ? modelboat.boatSides[0] : (modelPart.equals("back") ? modelboat.boatSides[1] : (modelPart.equals("front") ? modelboat.boatSides[2] : (modelPart.equals("right") ? modelboat.boatSides[3] : (modelPart.equals("left") ? modelboat.boatSides[4] : null))));
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"bottom", "back", "front", "right", "left"};
    }

    @Override
    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderBoat renderboat = new RenderBoat(rendermanager);
        if (!Reflector.RenderBoat_modelBoat.exists()) {
            Config.warn("Field not found: RenderBoat.modelBoat");
            return null;
        }
        Reflector.setFieldValue(renderboat, Reflector.RenderBoat_modelBoat, modelBase);
        renderboat.shadowSize = shadowSize;
        return renderboat;
    }
}

