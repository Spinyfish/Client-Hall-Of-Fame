package igbt.astolfy.events.listeners;

import igbt.astolfy.events.Event;
import igbt.astolfy.events.EventType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class EventMotion extends Event<EventMotion> {
	
	public double x,y,z;
	public float yaw,pitch;
	public boolean onGround;
	public EventType eventType;
	public EventMotion(EventType eventType,double x, double y, double z, float yaw, float pitch, boolean onGround) {
		super();
		this.eventType = eventType;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.onGround = onGround;
	}
	
    public float getDistanceToEntity(Entity entityIn)
    {
        float f = (float)(this.x - entityIn.posX);
        float f1 = (float)(this.y - entityIn.posY);
        float f2 = (float)(this.z - entityIn.posZ);
        return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
    }
    
    public float getEyeHeight()
    {
        float f = 1.62F;
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        if (p.isPlayerSleeping())
        {
            f = 0.2F;
        }

        if (p.isSneaking())
        {
            f -= 0.08F;
        }

        return f;
    }
	public double getX() {
		return x;
	}
	public EventType getEventType() {
		return eventType;
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
}
