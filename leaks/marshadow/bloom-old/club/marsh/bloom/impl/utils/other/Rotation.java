package club.marsh.bloom.impl.utils.other;

public class Rotation {
	private float yaw, pitch, lastPitch, lastYaw;
	
	public void setLastPitch(float lastPitch) {
		this.lastPitch = lastPitch;
	}

	public void setLastYaw(float lastYaw) {
		this.lastYaw = lastYaw;
	}

	public void setRotation(float yaw, float pitch) {
		this.lastPitch = this.pitch;
		this.lastYaw = this.yaw;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public float getYaw() {
		return this.yaw;
	}
	
	public float getPitch() {
		return this.pitch;
	}
	
	public float getLastYaw() {
		return this.yaw;
	}
	
	public float getLastPitch() {
		return this.pitch;
	}
	
	public Rotation(float yaw, float pitch) {
		this.lastYaw = this.yaw;
		this.lastPitch = this.pitch;
		this.yaw = yaw;
		this.pitch = pitch;
	}
}
