package alos.stella.event.events;

import alos.stella.event.Event;

public final class Render2DEvent extends Event {
    private final float partialTicks;

    public final float getPartialTicks() {
        return this.partialTicks;
    }

    public Render2DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
