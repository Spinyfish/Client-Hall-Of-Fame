package me.jinthium.straight.impl.event.render;

import me.jinthium.straight.api.event.Event;
import net.minecraft.client.gui.ScaledResolution;


public class Render2DEvent extends Event {
    private final float partialTicks;
    private final ScaledResolution scaledResolution;

    public Render2DEvent(float partialTicks, ScaledResolution scaledResolution) {
        this.partialTicks = partialTicks;
        this.scaledResolution = scaledResolution;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }
}
