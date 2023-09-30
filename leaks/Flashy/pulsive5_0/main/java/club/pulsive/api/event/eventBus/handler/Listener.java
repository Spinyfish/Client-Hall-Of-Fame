package club.pulsive.api.event.eventBus.handler;


import club.pulsive.api.event.Event;

public interface Listener<T extends Event> {

    /**
     * Method for calling an event
     *
     * @param event event that should be called
     */
    void call(final T event);
}
