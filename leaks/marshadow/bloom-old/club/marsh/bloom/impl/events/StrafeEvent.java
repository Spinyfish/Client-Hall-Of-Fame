package club.marsh.bloom.impl.events;

public class StrafeEvent extends Event {
	public float yaw, strafe, forward, friction;
	public StrafeEvent(float yaw, float strafe, float forward, float friction) {
		this.yaw = yaw;
		this.strafe = strafe;
		this.forward = forward;
		this.friction = friction;
	}
}
