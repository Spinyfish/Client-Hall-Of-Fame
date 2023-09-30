package felix.api.bus;

import felix.events.Event;

/**
 * @since 1.4.0
 */

public interface Bus<T extends Object> {

    void register(Object subscriber);

    void unregister(Object subscriber);

    void post(T event);
}
