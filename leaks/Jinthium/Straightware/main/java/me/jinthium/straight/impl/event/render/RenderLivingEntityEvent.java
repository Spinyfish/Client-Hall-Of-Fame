package me.jinthium.straight.impl.event.render;


import me.jinthium.straight.api.event.Event;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;


public class RenderLivingEntityEvent extends Event.StateEvent {
    private final EntityLivingBase entity;
    private final RendererLivingEntity<?> renderer;
    private final float partialTicks;
    private final double x,y,z;

    public RenderLivingEntityEvent(EntityLivingBase entity, RendererLivingEntity<?> renderer, float partialTicks, double x, double y, double z) {
        this.entity = entity;
        this.renderer = renderer;
        this.partialTicks = partialTicks;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }

    public RendererLivingEntity<?> getRenderer() {
        return renderer;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}