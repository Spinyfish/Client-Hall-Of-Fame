package felix.events.render;

import felix.events.Event;
import net.minecraft.client.gui.ScaledResolution;

public class EventRender2D implements Event {
	
    private ScaledResolution scaledResolution;
    private float partialTicks;

    public EventRender2D(ScaledResolution scaledResolution, float partialTicks) {
        this.scaledResolution = scaledResolution;
        this.partialTicks = partialTicks;
    }

    public ScaledResolution getScaledResolution() {
        return this.scaledResolution;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}