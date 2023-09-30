package wtf.automn.events.impl.combat;

import wtf.automn.events.events.Event;


public class EventClickMouse implements Event {
    private boolean cancel;

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

}
