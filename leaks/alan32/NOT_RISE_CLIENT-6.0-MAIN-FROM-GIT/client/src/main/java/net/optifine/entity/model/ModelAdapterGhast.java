package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelGhast;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderGhast;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterGhast extends ModelAdapter {
    public ModelAdapterGhast() {
        super(EntityGhast.class, "ghast", 0.5F);
    }

    public ModelBase makeModel() {
        return new ModelGhast();
    }

    public ModelRenderer getModelRenderer(final ModelBase model, final String modelPart) {
        if (!(model instanceof ModelGhast)) {
            return null;
        } else {
            final ModelGhast modelghast = (ModelGhast) model;

            if (modelPart.equals("body")) {
                return (ModelRenderer) Reflector.getFieldValue(modelghast, Reflector.ModelGhast_body);
            } else {
                final String s = "tentacle";

                if (modelPart.startsWith(s)) {
                    final ModelRenderer[] amodelrenderer = (ModelRenderer[]) Reflector.getFieldValue(modelghast, Reflector.ModelGhast_tentacles);

                    if (amodelrenderer == null) {
                        return null;
                    } else {
                        final String s1 = modelPart.substring(s.length());
                        int i = Config.parseInt(s1, -1);
                        --i;
                        return i >= 0 && i < amodelrenderer.length ? amodelrenderer[i] : null;
                    }
                } else {
                    return null;
                }
            }
        }
    }

    public String[] getModelRendererNames() {
        return new String[]{"body", "tentacle1", "tentacle2", "tentacle3", "tentacle4", "tentacle5", "tentacle6", "tentacle7", "tentacle8", "tentacle9"};
    }

    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderGhast renderghast = new RenderGhast(rendermanager);
        renderghast.mainModel = modelBase;
        renderghast.shadowSize = shadowSize;
        return renderghast;
    }
}
