package haptic.event.impl;

import haptic.event.Event;

public class MovementEvent extends Event {
	
	private double motionX, motionY, motionZ;

	public MovementEvent(double motionX, double motionY, double motionZ) {
		super(false);
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
	}

	public double getMotionX() {
		return motionX;
	}

	public void setMotionX(double motionX) {
		this.motionX = motionX;
	}

	public double getMotionY() {
		return motionY;
	}

	public void setMotionY(double motionY) {
		this.motionY = motionY;
	}

	public double getMotionZ() {
		return motionZ;
	}

	public void setMotionZ(double motionZ) {
		this.motionZ = motionZ;
	}
	
	
	
}