package client.event.bus;

public interface Bus<Event> {
    void register(final Object subscriber);

    void unregister(final Object subscriber);

    void handle(final Event event);
}
