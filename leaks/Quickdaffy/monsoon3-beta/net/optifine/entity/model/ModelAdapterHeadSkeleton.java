/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntitySkull;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterHeadSkeleton
extends ModelAdapter {
    public ModelAdapterHeadSkeleton() {
        super(TileEntitySkull.class, "head_skeleton", 0.0f);
    }

    @Override
    public ModelBase makeModel() {
        return new ModelSkeletonHead(0, 0, 64, 32);
    }

    @Override
    public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
        if (!(model instanceof ModelSkeletonHead)) {
            return null;
        }
        ModelSkeletonHead modelskeletonhead = (ModelSkeletonHead)model;
        return modelPart.equals("head") ? modelskeletonhead.skeletonHead : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"head"};
    }

    @Override
    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntitySkullRenderer tileentityspecialrenderer = tileentityrendererdispatcher.getSpecialRendererByClass(TileEntitySkull.class);
        if (!(tileentityspecialrenderer instanceof TileEntitySkullRenderer)) {
            return null;
        }
        if (tileentityspecialrenderer.getEntityClass() == null) {
            tileentityspecialrenderer = new TileEntitySkullRenderer();
            ((TileEntitySpecialRenderer)tileentityspecialrenderer).setRendererDispatcher(tileentityrendererdispatcher);
        }
        if (!Reflector.TileEntitySkullRenderer_humanoidHead.exists()) {
            Config.warn("Field not found: TileEntitySkullRenderer.humanoidHead");
            return null;
        }
        Reflector.setFieldValue(tileentityspecialrenderer, Reflector.TileEntitySkullRenderer_humanoidHead, modelBase);
        return tileentityspecialrenderer;
    }
}

