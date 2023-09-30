package intentions.events.listeners;

import intentions.events.Event;
import intentions.events.EventType;

public class EventMotion extends Event<EventMotion> {

	public double x, y, z;
	public float yaw, pitch;
	public boolean onGround;
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public boolean isOnGround() {
		return onGround;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	public EventMotion(double posX, double minY, double posZ, float rotationYaw, float rotationPitch, boolean onGround) {
		this.x = posX;
		this.y = minY;
		this.z = posZ;
		this.yaw = rotationYaw;
		this.pitch = rotationPitch;
		this.onGround = onGround;
	}
	
}
