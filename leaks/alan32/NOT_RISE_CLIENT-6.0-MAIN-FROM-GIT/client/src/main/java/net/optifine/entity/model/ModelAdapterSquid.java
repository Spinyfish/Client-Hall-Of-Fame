package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSquid;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterSquid extends ModelAdapter {
    public ModelAdapterSquid() {
        super(EntitySquid.class, "squid", 0.7F);
    }

    public ModelBase makeModel() {
        return new ModelSquid();
    }

    public ModelRenderer getModelRenderer(final ModelBase model, final String modelPart) {
        if (!(model instanceof ModelSquid)) {
            return null;
        } else {
            final ModelSquid modelsquid = (ModelSquid) model;

            if (modelPart.equals("body")) {
                return (ModelRenderer) Reflector.getFieldValue(modelsquid, Reflector.ModelSquid_body);
            } else {
                final String s = "tentacle";

                if (modelPart.startsWith(s)) {
                    final ModelRenderer[] amodelrenderer = (ModelRenderer[]) Reflector.getFieldValue(modelsquid, Reflector.ModelSquid_tentacles);

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
        return new String[]{"body", "tentacle1", "tentacle2", "tentacle3", "tentacle4", "tentacle5", "tentacle6", "tentacle7", "tentacle8"};
    }

    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderSquid rendersquid = new RenderSquid(rendermanager, modelBase, shadowSize);
        return rendersquid;
    }
}
