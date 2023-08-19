package dev.lemon.api.event.bus;

import dev.lemon.api.event.Event;

public interface IEventBus {
    void post(Event e);
    void subscribe(Object o);
    void unsubscribe(Object o);
}
