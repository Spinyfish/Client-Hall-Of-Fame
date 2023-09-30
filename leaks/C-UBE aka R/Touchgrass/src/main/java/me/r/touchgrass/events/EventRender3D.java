package me.r.touchgrass.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;

/**
 * Created by r on 10/02/2021
 */
public class EventRender3D extends EventCancellable {

    public static float partialTicks;

    public EventRender3D(float partialTicks)
    {
        EventRender3D.partialTicks = partialTicks;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }
}
