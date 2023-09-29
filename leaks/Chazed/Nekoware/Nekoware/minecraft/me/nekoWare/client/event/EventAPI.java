
package me.nekoWare.client.event;

import me.nekoWare.client.Nekoware;

public class EventAPI {
    public static void put(final Object o) {
        Nekoware.INSTANCE.getEventBus().subscribe(o);
    }

    public static void remove(final Object o) {
        Nekoware.INSTANCE.getEventBus().unsubscribe(o);
    }

    public static void fire(final Event event) {
        Nekoware.INSTANCE.getEventBus().post(event).dispatch();
    }
}
