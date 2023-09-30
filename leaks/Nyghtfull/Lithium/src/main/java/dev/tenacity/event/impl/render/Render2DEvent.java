package dev.tenacity.event.impl.render;

import dev.tenacity.event.Event;
import net.minecraft.client.gui.ScaledResolution;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class Render2DEvent extends Event {

    public ScaledResolution sr;
    private float width, height;

    public Render2DEvent(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public Render2DEvent(ScaledResolution sr) {
        this.sr = sr;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

}
