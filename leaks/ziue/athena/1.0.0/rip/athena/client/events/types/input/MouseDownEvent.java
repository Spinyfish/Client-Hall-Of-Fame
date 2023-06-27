package rip.athena.client.events.types.input;

import rip.athena.client.events.*;

public class MouseDownEvent extends Event
{
    private int button;
    
    public MouseDownEvent(final int button) {
        this.button = button;
    }
    
    public int getButton() {
        return this.button;
    }
}
