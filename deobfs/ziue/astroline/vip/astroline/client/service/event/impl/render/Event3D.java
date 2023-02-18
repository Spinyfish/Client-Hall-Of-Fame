/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.ScaledResolution
 *  vip.astroline.client.service.event.Event
 */
package vip.astroline.client.service.event.impl.render;

import net.minecraft.client.gui.ScaledResolution;
import vip.astroline.client.service.event.Event;

public class Event3D
extends Event {
    private final ScaledResolution scaledResolution;
    private final float partialTicks;

    public Event3D(ScaledResolution scaledResolution, float partialTicks) {
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
