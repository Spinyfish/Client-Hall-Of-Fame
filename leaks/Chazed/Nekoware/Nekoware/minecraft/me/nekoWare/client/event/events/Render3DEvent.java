
package me.nekoWare.client.event.events;

import me.nekoWare.client.event.Event;

public class Render3DEvent extends Event {
    private final float tick;

    public Render3DEvent(float tick) {
        this.tick = tick;
    }

    public float getRenderTick() {
        return this.tick;
    }
}
