package store.femboy.spring.impl.event;

import best.azura.eventbus.events.CancellableEvent;

public class Event extends CancellableEvent {

    public EventType type;

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public boolean isPre(){
        if(type == null)
            return false;

        return type == EventType.PRE;

    }

    public boolean isPost(){
        if(type == null)
            return false;

        return type == EventType.POST;

    }


}
