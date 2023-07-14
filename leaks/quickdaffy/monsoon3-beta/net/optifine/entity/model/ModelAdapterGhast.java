/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelGhast;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderGhast;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.src.Config;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterGhast
extends ModelAdapter {
    public ModelAdapterGhast() {
        super(EntityGhast.class, "ghast", 0.5f);
    }

    @Override
    public ModelBase makeModel() {
        return new ModelGhast();
    }

    @Override
    public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
        if (!(model instanceof ModelGhast)) {
            return null;
        }
        ModelGhast modelghast = (ModelGhast)model;
        if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.getFieldValue(modelghast, Reflector.ModelGhast_body);
        }
        String s = "tentacle";
        if (modelPart.startsWith(s)) {
            ModelRenderer[] amodelrenderer = (ModelRenderer[])Reflector.getFieldValue(modelghast, Reflector.ModelGhast_tentacles);
            if (amodelrenderer == null) {
                return null;
            }
            String s1 = modelPart.substring(s.length());
            int i = Config.parseInt(s1, -1);
            return --i >= 0 && i < amodelrenderer.length ? amodelrenderer[i] : null;
        }
        return null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"body", "tentacle1", "tentacle2", "tentacle3", "tentacle4", "tentacle5", "tentacle6", "tentacle7", "tentacle8", "tentacle9"};
    }

    @Override
    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderGhast renderghast = new RenderGhast(rendermanager);
        renderghast.mainModel = modelBase;
        renderghast.shadowSize = shadowSize;
        return renderghast;
    }
}

