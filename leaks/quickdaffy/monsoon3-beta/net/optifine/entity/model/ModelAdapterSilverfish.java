/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSilverfish;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.src.Config;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterSilverfish
extends ModelAdapter {
    public ModelAdapterSilverfish() {
        super(EntitySilverfish.class, "silverfish", 0.3f);
    }

    @Override
    public ModelBase makeModel() {
        return new ModelSilverfish();
    }

    @Override
    public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
        if (!(model instanceof ModelSilverfish)) {
            return null;
        }
        ModelSilverfish modelsilverfish = (ModelSilverfish)model;
        String s = "body";
        if (modelPart.startsWith(s)) {
            ModelRenderer[] amodelrenderer1 = (ModelRenderer[])Reflector.getFieldValue(modelsilverfish, Reflector.ModelSilverfish_bodyParts);
            if (amodelrenderer1 == null) {
                return null;
            }
            String s3 = modelPart.substring(s.length());
            int j = Config.parseInt(s3, -1);
            return --j >= 0 && j < amodelrenderer1.length ? amodelrenderer1[j] : null;
        }
        String s1 = "wing";
        if (modelPart.startsWith(s1)) {
            ModelRenderer[] amodelrenderer = (ModelRenderer[])Reflector.getFieldValue(modelsilverfish, Reflector.ModelSilverfish_wingParts);
            if (amodelrenderer == null) {
                return null;
            }
            String s2 = modelPart.substring(s1.length());
            int i = Config.parseInt(s2, -1);
            return --i >= 0 && i < amodelrenderer.length ? amodelrenderer[i] : null;
        }
        return null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"body1", "body2", "body3", "body4", "body5", "body6", "body7", "wing1", "wing2", "wing3"};
    }

    @Override
    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderSilverfish rendersilverfish = new RenderSilverfish(rendermanager);
        rendersilverfish.mainModel = modelBase;
        rendersilverfish.shadowSize = shadowSize;
        return rendersilverfish;
    }
}

