package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderMagmaCube;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterMagmaCube extends ModelAdapter {
    public ModelAdapterMagmaCube() {
        super(EntityMagmaCube.class, "magma_cube", 0.5F);
    }

    public ModelBase makeModel() {
        return new ModelMagmaCube();
    }

    public ModelRenderer getModelRenderer(final ModelBase model, final String modelPart) {
        if (!(model instanceof ModelMagmaCube)) {
            return null;
        } else {
            final ModelMagmaCube modelmagmacube = (ModelMagmaCube) model;

            if (modelPart.equals("core")) {
                return (ModelRenderer) Reflector.getFieldValue(modelmagmacube, Reflector.ModelMagmaCube_core);
            } else {
                final String s = "segment";

                if (modelPart.startsWith(s)) {
                    final ModelRenderer[] amodelrenderer = (ModelRenderer[]) Reflector.getFieldValue(modelmagmacube, Reflector.ModelMagmaCube_segments);

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
        return new String[]{"core", "segment1", "segment2", "segment3", "segment4", "segment5", "segment6", "segment7", "segment8"};
    }

    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderMagmaCube rendermagmacube = new RenderMagmaCube(rendermanager);
        rendermagmacube.mainModel = modelBase;
        rendermagmacube.shadowSize = shadowSize;
        return rendermagmacube;
    }
}
