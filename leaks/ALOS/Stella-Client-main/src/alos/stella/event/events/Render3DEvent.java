package alos.stella.event.events;

import alos.stella.event.Event;

public final class Render3DEvent extends Event {
    private final float partialTicks;

    public final float getPartialTicks() {
        return this.partialTicks;
    }

    public Render3DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
