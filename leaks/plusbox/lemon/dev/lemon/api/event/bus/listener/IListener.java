package dev.lemon.api.event.bus.listener;

@FunctionalInterface
public interface IListener<Event> {
    void post(Event e);
}