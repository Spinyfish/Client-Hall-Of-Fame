// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.events;

public class Event<T>
{
    public boolean cancelled;
    public EventType type;
    public EventDirection direction;
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public EventType getType() {
        return this.type;
    }
    
    public void setType(final EventType type) {
        this.type = type;
    }
    
    public EventDirection getDirection() {
        return this.direction;
    }
    
    public void setDirection(final EventDirection direction) {
        this.direction = direction;
    }
    
    public boolean isPre() {
        return this.type != null && this.type == EventType.PRE;
    }
    
    public boolean isPost() {
        return this.type != null && this.type == EventType.POST;
    }
    
    public boolean isIncoming() {
        return this.direction != null && this.direction == EventDirection.INCOMING;
    }
    
    public boolean isOutgoing() {
        return this.direction != null && this.direction == EventDirection.OUTGOING;
    }
}
