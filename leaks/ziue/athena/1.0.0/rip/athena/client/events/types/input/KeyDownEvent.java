package rip.athena.client.events.types.input;

import rip.athena.client.events.*;

public class KeyDownEvent extends Event
{
    private int key;
    
    public KeyDownEvent(final int key) {
        this.key = key;
    }
    
    public int getKey() {
        return this.key;
    }
}
