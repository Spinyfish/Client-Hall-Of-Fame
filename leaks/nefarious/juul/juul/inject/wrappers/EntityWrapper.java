package juul.inject.wrappers;

import juul.Juul;
import juul.inject.Wrapper;

public class EntityWrapper extends Wrapper {

	public EntityWrapper(Object instance) {
		super("net.minecraft.entity.Entity", instance);
	}

	public boolean isOnGround() { return (boolean) getField("onGround"); }

	public void jump() { getMethodPrimitive("jump"); }

	public double lastTickPosX() { return (double) getField("lastTickPosX"); }

	public double lastTickPosY() { return (double) getField("lastTickPosY"); }

	public double lastTickPosZ() { return (double) getField("lastTickPosZ"); }

	public double posX() { return (double) getField("posX"); }

	public double posY() { return (double) getField("posY"); }

	public double posZ() { return (double) getField("posZ"); }

	public float getEyeHeight() { return (float) getMethodPrimitive("getEyeHeight"); }

	public void rotationYaw(float yaw) { setField("rotationYaw", yaw); }

	public void rotationPitch(float pitch) { setField("rotationPitch", pitch); }

	public void prevRotationYaw(float yaw) { setField("prevRotationYaw", yaw); }

	public void prevRotationPitch(float pitch) { setField("prevRotationPitch", pitch); }

	public float rotationYaw() { return (float) getField("rotationYaw"); }

	public float rotationPitch() { return (float) getField("rotationPitch"); }

	public float getDistanceToEntity(Object entity) { return (float) callMethodWithTypes("getDistanceToEntity", new Object[] { entity }, getClassNameWithPath()); }

	public boolean isDead() { return (boolean) getField("isDead"); }

	public boolean isSprinting() { return (boolean) getMethodPrimitive("isSprinting"); }

	public boolean setSprinting(boolean sprinting) { return (boolean) getMethodPrimitive("setSprinting", sprinting); }

	public boolean isSneaking() { return (boolean) getMethodPrimitive("isSneaking"); }

}
