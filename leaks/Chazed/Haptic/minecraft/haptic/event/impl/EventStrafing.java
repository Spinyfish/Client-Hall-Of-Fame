package haptic.event.impl;

import haptic.event.Event;

public class EventStrafing extends Event {
	
	private float yaw;
	
	public EventStrafing(float yaw) {
		super(false);
		this.yaw = yaw;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

}