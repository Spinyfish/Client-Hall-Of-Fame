/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package wtf.monsoon.impl.event;

import lombok.NonNull;
import wtf.monsoon.api.event.Event;

public class EventKey
extends Event {
    @NonNull
    private int key;

    public EventKey(@NonNull int key) {
        this.key = key;
    }

    @NonNull
    public int getKey() {
        return this.key;
    }

    public void setKey(@NonNull int key) {
        this.key = key;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventKey)) {
            return false;
        }
        EventKey other = (EventKey)o;
        if (!other.canEqual(this)) {
            return false;
        }
        return this.getKey() == other.getKey();
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventKey;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getKey();
        return result;
    }

    public String toString() {
        return "EventKey(key=" + this.getKey() + ")";
    }
}

