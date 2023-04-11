package byron.Mono.event.impl;

import byron.Mono.event.Event;

public class EventKey extends Event { // This one is 100% implomented.


    private int key;


    public EventKey(int key)
    {
        this.key = key;
    }


    public int getKey()
    {
        return key;
    }

}
