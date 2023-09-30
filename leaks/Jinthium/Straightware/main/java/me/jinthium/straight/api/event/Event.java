package me.jinthium.straight.api.event;

import io.mxngo.echo.core.EventListener;

public class Event implements EventListener {
    private boolean isCancelled;

    public void cancel() {
        isCancelled = true;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public static class StateEvent extends Event {
        private boolean pre = true;

        
        public boolean isPre() { return pre;}
        
        public boolean isPost() { return !pre;}
        
        public void setPost() { pre = false; }
    }
}
