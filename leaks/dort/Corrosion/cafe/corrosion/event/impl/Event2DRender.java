package cafe.corrosion.event.impl;

import cafe.corrosion.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public class Event2DRender extends Event {
    private final int posX;
    private final int posY;
    private final float partialTicks;
    private final ScaledResolution scaledResolution;

    public int getPosX() {
        return this.posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public ScaledResolution getScaledResolution() {
        return this.scaledResolution;
    }

    public Event2DRender(int posX, int posY, float partialTicks, ScaledResolution scaledResolution) {
        this.posX = posX;
        this.posY = posY;
        this.partialTicks = partialTicks;
        this.scaledResolution = scaledResolution;
    }
}
