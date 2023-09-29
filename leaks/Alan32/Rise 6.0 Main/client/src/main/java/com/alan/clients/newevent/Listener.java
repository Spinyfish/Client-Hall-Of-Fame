package com.alan.clients.newevent;

@FunctionalInterface
public interface Listener<Event> {
    void call(Event event);
}