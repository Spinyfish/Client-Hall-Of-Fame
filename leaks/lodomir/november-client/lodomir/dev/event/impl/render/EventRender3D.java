/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.event.impl.render;

import lodomir.dev.event.EventUpdate;

public class EventRender3D
extends EventUpdate {
    private float partialTicks;

    public EventRender3D(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}

