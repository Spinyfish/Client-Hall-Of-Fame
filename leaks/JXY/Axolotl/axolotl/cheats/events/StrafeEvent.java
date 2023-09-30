package axolotl.cheats.events;

public class StrafeEvent extends Event {
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	public boolean getCancelled() {
		return cancelled;
	}
	
	public float getStrafe() {
		return strafe;
	}
	public void setStrafe(float strafe) {
		this.strafe = strafe;
	}
	public float getForward() {
		return forward;
	}
	public void setForward(float forward) {
		this.forward = forward;
	}

	public float strafe, forward;
	
	public StrafeEvent(float strafe, float forward, EventType event) {
		this.strafe = strafe;
		this.forward = forward;
	}
	
}
