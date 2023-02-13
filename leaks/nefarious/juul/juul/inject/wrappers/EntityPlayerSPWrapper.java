package juul.inject.wrappers;

import juul.inject.Wrapper;

public class EntityPlayerSPWrapper extends Wrapper {

	private final EntityWrapper entityWrapperObject;
	private final EntityLivingBaseWrapper entityLivingBaseWrapper;
	private final EntityPlayerWrapper entityPlayer;

	public EntityPlayerSPWrapper(Object instance) {
		super("net.minecraft.client.entity.EntityPlayerSP", instance);
		entityWrapperObject = new EntityWrapper(instance);
		entityLivingBaseWrapper	= new EntityLivingBaseWrapper(instance);
		entityPlayer = new EntityPlayerWrapper(instance);
	}
	
	public Object inventoryContainer() { return getField(entityPlayer.getPath(), "inventoryContainer"); }

	public boolean isSprinting() { return entityWrapperObject.isSprinting(); }

	public void setSprinting(boolean state) { entityWrapperObject.setSprinting(state); }
	
	public boolean isSneaking() { return entityWrapperObject.isSneaking(); }

	public float getEyeHeight() {
		return entityWrapperObject.getEyeHeight();
	}
	
	public float rotationYaw() {
		return entityWrapperObject.rotationYaw();
	}
	
	public float rotationPitch() {
		return entityWrapperObject.rotationPitch();
	}

	public boolean getOnGround() { return entityWrapperObject.isOnGround(); }

	public int getHurtTime() { return entityLivingBaseWrapper.getHurtTime(); }

	public void jump() { entityWrapperObject.jump(); }


	public EntityWrapper getEntityWrapperObject() { return entityWrapperObject; }

	public EntityLivingBaseWrapper getEntityLivingBaseWrapper() { return entityLivingBaseWrapper; }

	public EntityPlayerWrapper getEntityPlayer() { return entityPlayer; }


}
