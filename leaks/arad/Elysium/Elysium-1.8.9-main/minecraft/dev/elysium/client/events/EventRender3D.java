package dev.elysium.client.events;

import dev.elysium.base.events.types.Event;

public class EventRender3D extends Event {
    public float partialTicks;

    public EventRender3D(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public void setCode(float partialTicks) {
        this.partialTicks = partialTicks;
    }

}
