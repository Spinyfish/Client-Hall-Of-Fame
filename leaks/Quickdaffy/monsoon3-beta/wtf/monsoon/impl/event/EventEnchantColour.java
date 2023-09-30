/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package wtf.monsoon.impl.event;

import java.awt.Color;
import lombok.NonNull;
import wtf.monsoon.api.event.Event;

public class EventEnchantColour
extends Event {
    @NonNull
    private Color colour;

    public EventEnchantColour(@NonNull Color colour) {
        if (colour == null) {
            throw new NullPointerException("colour is marked non-null but is null");
        }
        this.colour = colour;
    }

    @NonNull
    public Color getColour() {
        return this.colour;
    }

    public void setColour(@NonNull Color colour) {
        if (colour == null) {
            throw new NullPointerException("colour is marked non-null but is null");
        }
        this.colour = colour;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventEnchantColour)) {
            return false;
        }
        EventEnchantColour other = (EventEnchantColour)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Color this$colour = this.getColour();
        Color other$colour = other.getColour();
        return !(this$colour == null ? other$colour != null : !((Object)this$colour).equals(other$colour));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventEnchantColour;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Color $colour = this.getColour();
        result = result * 59 + ($colour == null ? 43 : ((Object)$colour).hashCode());
        return result;
    }

    public String toString() {
        return "EventEnchantColour(colour=" + this.getColour() + ")";
    }
}

