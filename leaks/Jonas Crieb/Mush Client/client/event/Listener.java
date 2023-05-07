package client.event;

@FunctionalInterface
public interface Listener<Event> {
    void call(Event event);
}
