package dev.africa.pandaware.api.event.interfaces;

import dev.africa.pandaware.api.event.Event;

public interface EventCallback<T extends Event> {
    void invokeEvent(T event);
}