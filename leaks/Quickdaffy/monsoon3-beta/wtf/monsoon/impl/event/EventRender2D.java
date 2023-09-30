/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package wtf.monsoon.impl.event;

import lombok.NonNull;
import net.minecraft.client.gui.ScaledResolution;
import wtf.monsoon.api.event.Event;

public class EventRender2D
extends Event {
    @NonNull
    private ScaledResolution sr;
    @NonNull
    private float partialTicks;
    @NonNull
    private float width;
    @NonNull
    private float height;

    public EventRender2D(@NonNull ScaledResolution sr, @NonNull float partialTicks, @NonNull float width, @NonNull float height) {
        if (sr == null) {
            throw new NullPointerException("sr is marked non-null but is null");
        }
        this.sr = sr;
        this.partialTicks = partialTicks;
        this.width = width;
        this.height = height;
    }

    @NonNull
    public ScaledResolution getSr() {
        return this.sr;
    }

    @NonNull
    public float getPartialTicks() {
        return this.partialTicks;
    }

    @NonNull
    public float getWidth() {
        return this.width;
    }

    @NonNull
    public float getHeight() {
        return this.height;
    }

    public void setSr(@NonNull ScaledResolution sr) {
        if (sr == null) {
            throw new NullPointerException("sr is marked non-null but is null");
        }
        this.sr = sr;
    }

    public void setPartialTicks(@NonNull float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public void setWidth(@NonNull float width) {
        this.width = width;
    }

    public void setHeight(@NonNull float height) {
        this.height = height;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventRender2D)) {
            return false;
        }
        EventRender2D other = (EventRender2D)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Float.compare(this.getPartialTicks(), other.getPartialTicks()) != 0) {
            return false;
        }
        if (Float.compare(this.getWidth(), other.getWidth()) != 0) {
            return false;
        }
        if (Float.compare(this.getHeight(), other.getHeight()) != 0) {
            return false;
        }
        ScaledResolution this$sr = this.getSr();
        ScaledResolution other$sr = other.getSr();
        return !(this$sr == null ? other$sr != null : !this$sr.equals(other$sr));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventRender2D;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Float.floatToIntBits(this.getPartialTicks());
        result = result * 59 + Float.floatToIntBits(this.getWidth());
        result = result * 59 + Float.floatToIntBits(this.getHeight());
        ScaledResolution $sr = this.getSr();
        result = result * 59 + ($sr == null ? 43 : $sr.hashCode());
        return result;
    }

    public String toString() {
        return "EventRender2D(sr=" + this.getSr() + ", partialTicks=" + this.getPartialTicks() + ", width=" + this.getWidth() + ", height=" + this.getHeight() + ")";
    }
}

