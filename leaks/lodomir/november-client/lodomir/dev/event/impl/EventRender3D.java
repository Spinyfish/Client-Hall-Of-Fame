/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.event.impl;

import lodomir.dev.event.Event;

public class EventRender3D
extends Event {
    private float partialTicks;

    public EventRender3D(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}

