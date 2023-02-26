package ooo.cpacket.ruby.api.event.events.player;

import ooo.cpacket.ruby.api.event.IEvent;
import ooo.cpacket.ruby.api.event.events.player.motion.State;

public class EventMotionUpdate implements IEvent {
	
	private double posX;
	
	private double posY;
	
	private double posZ;
	
	private float rotationYaw;
	
	private float rotationPitch;
	
	private boolean onGround;
	
	private State state;
	
	public EventMotionUpdate(double posX, double posY, double posZ, float rotationYaw, float rotationPitch, boolean onGround) {
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.rotationYaw = rotationYaw;
		this.rotationPitch = rotationPitch;
		this.onGround = onGround;
		this.state = State.PRE;
	}
	
	public EventMotionUpdate() {
		this.state = State.POST;
	}

	public double getPosX() {
		return posX;
	}

	public double getPosY() {
		return posY;
	}

	public double getPosZ() {
		return posZ;
	}

	public boolean isOnGround() {
		return onGround;
	}

	public State getState() {
		return state;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public void setPosY(double posY) {
		this.posY = posY;
	}

	public void setPosZ(double posZ) {
		this.posZ = posZ;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	public void setState(State state) {
		this.state = state;
	}

	public float getYaw() {
		return this.rotationYaw;
	}

	public float getPitch() {
		return this.rotationPitch;
	}

	public void setYaw(float yaw) {		
		this.rotationYaw = yaw;
	}
	public void setPitch(float pitch) {
		this.rotationPitch = pitch;
	}
}
