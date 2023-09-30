/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.event;

public class Event {
    private boolean cancelled;

    public void cancel() {
        this.cancelled = true;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

