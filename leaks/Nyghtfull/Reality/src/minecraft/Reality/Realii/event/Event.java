/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.event;

public abstract class Event {
    private boolean cancelled;
    public byte type;

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public byte getType() {
        return this.type;
    }

    public void setType(byte type) {
        this.type = type;
    }
}

