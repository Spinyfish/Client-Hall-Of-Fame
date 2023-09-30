/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.entity.monster.EntitySlime;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterSlime
extends ModelAdapter {
    public ModelAdapterSlime() {
        super(EntitySlime.class, "slime", 0.25f);
    }

    @Override
    public ModelBase makeModel() {
        return new ModelSlime(16);
    }

    @Override
    public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
        if (!(model instanceof ModelSlime)) {
            return null;
        }
        ModelSlime modelslime = (ModelSlime)model;
        return modelPart.equals("body") ? (ModelRenderer)Reflector.getFieldValue(modelslime, Reflector.ModelSlime_ModelRenderers, 0) : (modelPart.equals("left_eye") ? (ModelRenderer)Reflector.getFieldValue(modelslime, Reflector.ModelSlime_ModelRenderers, 1) : (modelPart.equals("right_eye") ? (ModelRenderer)Reflector.getFieldValue(modelslime, Reflector.ModelSlime_ModelRenderers, 2) : (modelPart.equals("mouth") ? (ModelRenderer)Reflector.getFieldValue(modelslime, Reflector.ModelSlime_ModelRenderers, 3) : null)));
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"body", "left_eye", "right_eye", "mouth"};
    }

    @Override
    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderSlime renderslime = new RenderSlime(rendermanager, modelBase, shadowSize);
        return renderslime;
    }
}

