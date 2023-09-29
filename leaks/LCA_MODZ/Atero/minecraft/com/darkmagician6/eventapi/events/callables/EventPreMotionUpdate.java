package com.darkmagician6.eventapi.events.callables;

public class EventPreMotionUpdate extends EventCancellable {
	public static EventPreMotionUpdate getInstance;
	private static float yaw, pitch;
	public static float lastYaw, lastPitch;
	private boolean ground;
	public double x, y, z;
	private double motionX;
	private double MotionZ;

	public EventPreMotionUpdate(float yaw, float pitch, boolean ground, double x, double y, double z, double motionX, double MotionZ) {
		this.lastYaw = this.yaw;
		this.lastPitch = this.pitch;
		this.yaw = yaw;
		this.pitch = pitch;
		this.ground = ground;
		this.x = x;
		this.y = y;
		this.z = z;
		this.motionX = motionX;
		this.MotionZ = MotionZ;
		getInstance = this;
	}

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

	public double getMotionX() {
		return motionX;
	}

	public void setMotionX(double motionX) {
		this.motionX = motionX;
	}

	public double getMotionZ() {
		return MotionZ;
	}

	public void setMotionZ(double motionZ) {
		MotionZ = motionZ;
	}

	public boolean isGround() {
		return ground;
	}

	@Override
	public boolean isCancelled() {
		return super.isCancelled();
	}

	@Override
	public void setCancelled(boolean state) {
		super.setCancelled(state);
	}

	public static float getYaw() {
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

	public boolean onGround() {
		return ground;
	}

	public void setGround(boolean ground) {
		this.ground = ground;
	}

	public byte getType() {
		return 0;
	}
}
