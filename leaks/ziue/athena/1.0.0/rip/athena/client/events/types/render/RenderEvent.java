package rip.athena.client.events.types.render;

import rip.athena.client.events.*;

public class RenderEvent extends Event
{
    public RenderType type;
    public float partialTicks;
    
    public RenderEvent(final RenderType type) {
        this.type = type;
    }
    
    public RenderEvent(final RenderType type, final float partialTicks) {
        this.type = type;
        this.partialTicks = partialTicks;
    }
    
    public RenderType getRenderType() {
        return this.type;
    }
    
    public float getPartialTicks() {
        return this.partialTicks;
    }
}
