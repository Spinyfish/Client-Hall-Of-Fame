// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.events.listeners;

import today.getbypass.events.Event;

public class EventChat extends Event<EventChat>
{
    public String message;
    
    public EventChat(final String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
}
