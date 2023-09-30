/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSheep1;
import net.minecraft.client.model.ModelSheep2;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSheep;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerSheepWool;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.src.Config;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterQuadruped;

public class ModelAdapterSheepWool
extends ModelAdapterQuadruped {
    public ModelAdapterSheepWool() {
        super(EntitySheep.class, "sheep_wool", 0.7f);
    }

    @Override
    public ModelBase makeModel() {
        return new ModelSheep1();
    }

    @Override
    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        Render render = rendermanager.getEntityRenderMap().get(EntitySheep.class);
        if (!(render instanceof RenderSheep)) {
            Config.warn("Not a RenderSheep: " + render);
            return null;
        }
        if (render.getEntityClass() == null) {
            render = new RenderSheep(rendermanager, new ModelSheep2(), 0.7f);
        }
        RenderSheep rendersheep = (RenderSheep)render;
        List list = rendersheep.getLayerRenderers();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            LayerRenderer layerrenderer = iterator.next();
            if (!(layerrenderer instanceof LayerSheepWool)) continue;
            iterator.remove();
        }
        LayerSheepWool layersheepwool = new LayerSheepWool(rendersheep);
        layersheepwool.sheepModel = (ModelSheep1)modelBase;
        rendersheep.addLayer(layersheepwool);
        return rendersheep;
    }
}

