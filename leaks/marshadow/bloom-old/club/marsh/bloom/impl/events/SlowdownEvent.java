package club.marsh.bloom.impl.events;

public class SlowdownEvent extends Event {
	public float forward, strafe;
	public boolean stopSprint;
	public SlowdownEvent(float forward, float strafe, boolean stopSprint) {
		this.forward = forward;
		this.strafe = strafe;
		this.stopSprint = stopSprint;
	}
}
