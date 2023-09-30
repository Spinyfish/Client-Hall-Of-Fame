package me.jinthium.straight.impl.event.render;


import me.jinthium.straight.api.event.Event;

public class RenderTickEvent extends Event.StateEvent {
    private final float ticks;

    public RenderTickEvent(float ticks) {
        this.ticks = ticks;
    }

    public float getTicks() {
        return ticks;
    }
}
