package club.pulsive.api.event.eventBus.handler;


import club.pulsive.api.event.eventBus.core.EventPriority;

import java.util.ArrayList;

public class EventObject {
    private final Object object;
    private final EventPriority eventPriority;
    private final ArrayList<EventExecutable> eventExecutables;

    public EventObject(Object object, EventPriority eventPriority) {
        this.object = object;
        this.eventPriority = eventPriority;
        this.eventExecutables = new ArrayList<>();
    }


    public Object getObject() {
        return object;
    }

    public EventPriority getEventPriority() {
        return eventPriority;
    }

    public ArrayList<EventExecutable> getEventExecutables() {
        return eventExecutables;
    }
}