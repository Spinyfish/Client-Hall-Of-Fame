package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderMite;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderEndermite;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterEndermite extends ModelAdapter {
    public ModelAdapterEndermite() {
        super(EntityEndermite.class, "endermite", 0.3F);
    }

    public ModelBase makeModel() {
        return new ModelEnderMite();
    }

    public ModelRenderer getModelRenderer(final ModelBase model, final String modelPart) {
        if (!(model instanceof ModelEnderMite)) {
            return null;
        } else {
            final ModelEnderMite modelendermite = (ModelEnderMite) model;
            final String s = "body";

            if (modelPart.startsWith(s)) {
                final ModelRenderer[] amodelrenderer = (ModelRenderer[]) Reflector.getFieldValue(modelendermite, Reflector.ModelEnderMite_bodyParts);

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

    public String[] getModelRendererNames() {
        return new String[]{"body1", "body2", "body3", "body4"};
    }

    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderEndermite renderendermite = new RenderEndermite(rendermanager);
        renderendermite.mainModel = modelBase;
        renderendermite.shadowSize = shadowSize;
        return renderendermite;
    }
}
