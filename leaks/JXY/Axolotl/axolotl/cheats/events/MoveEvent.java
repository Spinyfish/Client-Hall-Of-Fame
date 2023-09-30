package axolotl.cheats.events;

public class MoveEvent extends Event {

	private boolean onGround;
	private double x, y, z;
	private float yaw, pitch;
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	public boolean getCancelled() {
		return cancelled;
	}
	
	public MoveEvent(double x, double y, double z, float yaw, float pitch, boolean onGround, EventType eventType) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.onGround = onGround;
		this.yaw = yaw;
		this.pitch = pitch;
		this.eventType = eventType;
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
	public boolean getOnGround() {
		return onGround;
	}
	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}
	
}
