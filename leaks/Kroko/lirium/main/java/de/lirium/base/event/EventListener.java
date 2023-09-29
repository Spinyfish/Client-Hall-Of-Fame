package de.lirium.base.event;

import de.lirium.Client;

public class EventListener {
    public void init() {
        Client.INSTANCE.getEventBus().register(this);
    }
}
