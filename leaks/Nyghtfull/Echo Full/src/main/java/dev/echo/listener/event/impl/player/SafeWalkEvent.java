package dev.echo.listener.event.impl.player;

import dev.echo.listener.event.Event;


public class SafeWalkEvent extends Event {

    private boolean safe;

    public boolean isSafe() {
        return this.safe;
    }

   
    public void setSafe(boolean safe) {
        this.safe = safe;
    }

}
