package io.github.raze.events.collection.visual;

import io.github.raze.events.system.BaseEvent;
import net.minecraft.client.gui.ScaledResolution;

public class EventRender2D extends BaseEvent {

    public ScaledResolution scaledResolution;
    public float partialTicks;

    public EventRender2D(ScaledResolution scaledResolution, float partialTicks) {
        this.scaledResolution = scaledResolution;
        this.partialTicks = partialTicks;
    }

    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
