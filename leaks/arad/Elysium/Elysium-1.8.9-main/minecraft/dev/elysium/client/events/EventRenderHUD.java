package dev.elysium.client.events;

import dev.elysium.base.events.types.Event;

public class EventRenderHUD extends Event{

    double partialTicks;

    public EventRenderHUD(double partialTicks)
    {
        this.partialTicks = partialTicks;
    }

    public double getPartialTicks() {
        return partialTicks;
    }

    public void setPartialTicks(double partialTicks) {
        this.partialTicks = partialTicks;
    }
}
