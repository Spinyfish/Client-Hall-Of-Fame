package byron.Mono.event.impl;

import byron.Mono.event.Event;

public class Event3D extends Event { // This one is NOT implomented.

    public static float partialTicks;

    public static float getPartialTicks ()
    {
        return partialTicks;

    }

    public void setPartialTicks (float partialTicks)
    {
        this.partialTicks = partialTicks;

    }
    
}
