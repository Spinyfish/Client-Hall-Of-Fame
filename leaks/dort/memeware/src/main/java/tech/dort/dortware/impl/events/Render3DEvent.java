package tech.dort.dortware.impl.events;

import tech.dort.dortware.api.event.Event;

public class Render3DEvent extends Event {
    private final float ticks;

    public Render3DEvent(float ticks) {
        this.ticks = ticks;
    }

    public float getPartialTicks() {
        return ticks;
    }
}
