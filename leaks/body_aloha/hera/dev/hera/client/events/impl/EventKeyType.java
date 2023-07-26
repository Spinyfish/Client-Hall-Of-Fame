package dev.hera.client.events.impl;

import dev.hera.client.events.types.Event;

public class EventKeyType extends Event {
    public final int keyCode;
    public EventKeyType(int keyCode){
        this.keyCode = keyCode;
    }
}