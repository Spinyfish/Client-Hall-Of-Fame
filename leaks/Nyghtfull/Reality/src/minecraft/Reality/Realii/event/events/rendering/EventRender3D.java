/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.event.events.rendering;

import shadersmod.client.Shaders;
import Reality.Realii.event.Event;

public class EventRender3D
extends Event {
    private float ticks;
    private boolean isUsingShaders;

    public EventRender3D() {
        this.isUsingShaders = Shaders.getShaderPackName() != null;
    }

    public EventRender3D(float ticks) {
        this.ticks = ticks;
        this.isUsingShaders = Shaders.getShaderPackName() != null;
    }

    public float getPartialTicks() {
        return this.ticks;
    }

    public boolean isUsingShaders() {
        return this.isUsingShaders;
    }
}

