/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package wtf.monsoon.impl.event;

import lombok.NonNull;
import wtf.monsoon.api.event.Event;

public class EventRender3D
extends Event {
    @NonNull
    private float partialTicks;

    public EventRender3D(@NonNull float partialTicks) {
        this.partialTicks = partialTicks;
    }

    @NonNull
    public float getPartialTicks() {
        return this.partialTicks;
    }

    public void setPartialTicks(@NonNull float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventRender3D)) {
            return false;
        }
        EventRender3D other = (EventRender3D)o;
        if (!other.canEqual(this)) {
            return false;
        }
        return Float.compare(this.getPartialTicks(), other.getPartialTicks()) == 0;
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventRender3D;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Float.floatToIntBits(this.getPartialTicks());
        return result;
    }

    public String toString() {
        return "EventRender3D(partialTicks=" + this.getPartialTicks() + ")";
    }
}

