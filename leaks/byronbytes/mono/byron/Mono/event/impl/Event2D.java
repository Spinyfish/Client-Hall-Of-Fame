package byron.Mono.event.impl;

import byron.Mono.event.Event;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

public class Event2D extends Event { // This one is 100% implomented.

    private float partialTicks;
    private ScaledResolution scaledResolution;
    private FontRenderer fontRenderer;

    public Event2D(ScaledResolution scaledResolution, float partialTicks, FontRenderer fontRenderer)
    {
        this.scaledResolution = scaledResolution;
        this.partialTicks = partialTicks;
        this.fontRenderer = fontRenderer;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }

    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }


}
