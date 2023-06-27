package rip.athena.client.events.types.render;

import rip.athena.client.events.*;
import net.minecraft.entity.player.*;

public class RenderPlayerEvent extends Event
{
    private EntityPlayer player;
    private double x;
    private double y;
    private double z;
    private float partialTicks;
    
    public RenderPlayerEvent(final EntityPlayer player, final double x, final double y, final double z, final float partialTicks) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
        this.partialTicks = partialTicks;
    }
    
    public EntityPlayer getPlayer() {
        return this.player;
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
    
    public float getPartialTicks() {
        return this.partialTicks;
    }
}
