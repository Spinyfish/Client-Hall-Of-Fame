package dev.echo.listener;

@FunctionalInterface
public interface Listener<Event> {
    void call(Event event);
}