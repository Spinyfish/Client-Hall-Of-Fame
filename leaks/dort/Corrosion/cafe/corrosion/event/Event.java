package cafe.corrosion.event;

public abstract class Event {
    protected boolean cancelled;

    public boolean isCancelled() {
        return this.cancelled;
    }
}
