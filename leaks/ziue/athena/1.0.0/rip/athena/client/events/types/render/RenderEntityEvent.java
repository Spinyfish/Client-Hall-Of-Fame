package rip.athena.client.events.types.render;

import net.minecraft.entity.*;
import rip.athena.client.events.*;
import net.minecraft.client.renderer.entity.*;

public class RenderEntityEvent<T extends EntityLivingBase> extends Event
{
    private T entity;
    private RendererLivingEntity<T> renderer;
    private double x;
    private double y;
    private double z;
    
    public RenderEntityEvent(final T entity, final RendererLivingEntity<T> renderer, final double x, final double y, final double z) {
        this.entity = entity;
        this.renderer = renderer;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public T getEntity() {
        return this.entity;
    }
    
    public RendererLivingEntity<T> getRenderer() {
        return this.renderer;
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public double getZ() {
        return this.z;
    }
}
