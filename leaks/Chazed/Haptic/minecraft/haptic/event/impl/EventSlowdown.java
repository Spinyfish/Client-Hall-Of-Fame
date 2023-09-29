package haptic.event.impl;

import haptic.event.Event;

public class EventSlowdown extends Event {
	
	private float forward, strafe;

	public EventSlowdown(float forward, float strafe) {
		super(false);
		this.forward = forward;
		this.strafe = strafe;
	}
	
	public float getForward() {
		return forward;
	}
	
	public void setForward(float forward) {
		this.forward = forward;
	}
	
	public float getStrafe() {
		return strafe;
	}
	
	public void setStrafe(float strafe) {
		this.strafe = strafe;
	}
	
}