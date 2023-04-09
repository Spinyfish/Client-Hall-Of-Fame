package tech.dort.dortware.api.event;

public abstract class Event {

    private boolean cancelled;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void forceCancel(boolean condition) {
        if (!this.cancelled) {
            this.cancelled = condition;
        }
    }
}
