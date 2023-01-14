package cafe.corrosion.event.impl;

import cafe.corrosion.event.Event;

public class Event3DRender extends Event {
    private final float partialTicks;

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public Event3DRender(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
