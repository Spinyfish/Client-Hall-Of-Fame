package com.darkmagician6.eventapi.events.callables;

public class EventPostMotionUpdate extends EventCancellable {

    public EventPostMotionUpdate() {
    }

    @Override
    public boolean isCancelled() {
	return super.isCancelled();
    }

    @Override
    public void setCancelled(boolean state) {
	super.setCancelled(state);
    }

    public byte getType() {
	return 0;
    }
}
