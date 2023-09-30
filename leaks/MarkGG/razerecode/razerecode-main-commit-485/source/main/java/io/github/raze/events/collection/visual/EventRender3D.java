package io.github.raze.events.collection.visual;

import io.github.raze.events.system.BaseEvent;

public class EventRender3D extends BaseEvent {

    public float partialTicks;

    public EventRender3D(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
