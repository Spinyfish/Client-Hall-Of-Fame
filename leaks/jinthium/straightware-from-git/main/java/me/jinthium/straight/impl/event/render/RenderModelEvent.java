package me.jinthium.straight.impl.event.render;

import me.jinthium.straight.api.event.Event;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;


public class RenderModelEvent extends Event.StateEvent {

    private final EntityLivingBase entity;
    private final Runnable modelRenderer;
    private final Runnable layerRenderer;

    public RenderModelEvent(EntityLivingBase entity, Runnable modelRenderer, Runnable layerRenderer) {
        this.entity = entity;
        this.modelRenderer = modelRenderer;
        this.layerRenderer = layerRenderer;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }

    public Runnable getModelRenderer() {
        return modelRenderer;
    }

    public void drawModel() {
        this.modelRenderer.run();
    }

    public void drawLayers() {
        this.layerRenderer.run();
    }

}
