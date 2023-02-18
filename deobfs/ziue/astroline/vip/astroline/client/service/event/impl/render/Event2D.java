/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.event.Event
 */
package vip.astroline.client.service.event.impl.render;

import vip.astroline.client.service.event.Event;

public class Event2D
extends Event {
    private float width;
    private float height;
    float partialTicks;

    public Event2D(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getTicks() {
        return this.partialTicks;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }
}
