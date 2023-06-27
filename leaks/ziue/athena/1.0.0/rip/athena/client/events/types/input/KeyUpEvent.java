package rip.athena.client.events.types.input;

import rip.athena.client.events.*;

public class KeyUpEvent extends Event
{
    private int key;
    
    public KeyUpEvent(final int key) {
        this.key = key;
    }
    
    public int getKey() {
        return this.key;
    }
}
