/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.event;

import net.minecraft.client.gui.ScaledResolution;
import wtf.monsoon.api.event.Event;

public class EventRenderHotbar
extends Event {
    ScaledResolution sr;

    public ScaledResolution getSr() {
        return this.sr;
    }

    public EventRenderHotbar(ScaledResolution sr) {
        this.sr = sr;
    }

    public static class Pre
    extends EventRenderHotbar {
        ScaledResolution sr;

        public Pre(ScaledResolution sr) {
            super(sr);
        }

        @Override
        public ScaledResolution getSr() {
            return this.sr;
        }
    }
}

