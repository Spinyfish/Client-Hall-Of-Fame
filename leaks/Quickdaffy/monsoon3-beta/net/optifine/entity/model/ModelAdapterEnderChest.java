/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityEnderChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterEnderChest
extends ModelAdapter {
    public ModelAdapterEnderChest() {
        super(TileEntityEnderChest.class, "ender_chest", 0.0f);
    }

    @Override
    public ModelBase makeModel() {
        return new ModelChest();
    }

    @Override
    public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
        if (!(model instanceof ModelChest)) {
            return null;
        }
        ModelChest modelchest = (ModelChest)model;
        return modelPart.equals("lid") ? modelchest.chestLid : (modelPart.equals("base") ? modelchest.chestBelow : (modelPart.equals("knob") ? modelchest.chestKnob : null));
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"lid", "base", "knob"};
    }

    @Override
    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntityEnderChestRenderer tileentityspecialrenderer = tileentityrendererdispatcher.getSpecialRendererByClass(TileEntityEnderChest.class);
        if (!(tileentityspecialrenderer instanceof TileEntityEnderChestRenderer)) {
            return null;
        }
        if (tileentityspecialrenderer.getEntityClass() == null) {
            tileentityspecialrenderer = new TileEntityEnderChestRenderer();
            tileentityspecialrenderer.setRendererDispatcher(tileentityrendererdispatcher);
        }
        if (!Reflector.TileEntityEnderChestRenderer_modelChest.exists()) {
            Config.warn("Field not found: TileEntityEnderChestRenderer.modelChest");
            return null;
        }
        Reflector.setFieldValue(tileentityspecialrenderer, Reflector.TileEntityEnderChestRenderer_modelChest, modelBase);
        return tileentityspecialrenderer;
    }
}

