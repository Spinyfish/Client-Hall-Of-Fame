package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelWither;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderWither;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterWither extends ModelAdapter {
    public ModelAdapterWither() {
        super(EntityWither.class, "wither", 0.5F);
    }

    public ModelBase makeModel() {
        return new ModelWither(0.0F);
    }

    public ModelRenderer getModelRenderer(final ModelBase model, final String modelPart) {
        if (!(model instanceof ModelWither)) {
            return null;
        } else {
            final ModelWither modelwither = (ModelWither) model;
            final String s = "body";

            if (modelPart.startsWith(s)) {
                final ModelRenderer[] amodelrenderer1 = (ModelRenderer[]) Reflector.getFieldValue(modelwither, Reflector.ModelWither_bodyParts);

                if (amodelrenderer1 == null) {
                    return null;
                } else {
                    final String s3 = modelPart.substring(s.length());
                    int j = Config.parseInt(s3, -1);
                    --j;
                    return j >= 0 && j < amodelrenderer1.length ? amodelrenderer1[j] : null;
                }
            } else {
                final String s1 = "head";

                if (modelPart.startsWith(s1)) {
                    final ModelRenderer[] amodelrenderer = (ModelRenderer[]) Reflector.getFieldValue(modelwither, Reflector.ModelWither_heads);

                    if (amodelrenderer == null) {
                        return null;
                    } else {
                        final String s2 = modelPart.substring(s1.length());
                        int i = Config.parseInt(s2, -1);
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
        return new String[]{"body1", "body2", "body3", "head1", "head2", "head3"};
    }

    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderWither renderwither = new RenderWither(rendermanager);
        renderwither.mainModel = modelBase;
        renderwither.shadowSize = shadowSize;
        return renderwither;
    }
}
