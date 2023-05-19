package club.marsh.bloom.impl.events;

public class JumpEvent extends Event {
	public float yaw;
	public JumpEvent(float yaw) {
		this.yaw = yaw;
	}
}
