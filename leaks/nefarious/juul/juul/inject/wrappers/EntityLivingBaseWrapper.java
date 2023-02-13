package juul.inject.wrappers;

import java.awt.image.BufferedImage;

import juul.Juul;
import juul.inject.LUT.Version;
import juul.inject.Wrapper;

public class EntityLivingBaseWrapper extends Wrapper {

    private final EntityWrapper entityWrapperObject;

    public EntityLivingBaseWrapper(Object instance) {
        super("net.minecraft.entity.EntityLivingBase", instance);
        entityWrapperObject = new EntityWrapper(instance);
    }

    public int getHurtTime() { return (int) getField("hurtTime"); }

    public float moveForward() { return (float) getField("moveForward"); }

    public float moveStrafing() { return (float) getField("moveStrafing"); }

    public float getHealth() { return (float) getMethodPrimitive("getHealth"); }

    public double lastTickPosX() {
        return entityWrapperObject.lastTickPosX();
    }

    public double lastTickPosY() {
        return entityWrapperObject.lastTickPosY();
    }

    public double lastTickPosZ() {
        return entityWrapperObject.lastTickPosZ();
    }

    public double posX() {
        return entityWrapperObject.posX();
    }

    public double posY() {
        return entityWrapperObject.posY();
    }

    public double posZ() {
        return entityWrapperObject.posZ();
    }

    public float getEyeHeight() {
        return entityWrapperObject.getEyeHeight();
    }

    public float rotationYaw() {
        return entityWrapperObject.rotationYaw();
    }

    public float rotationPitch() {
        return entityWrapperObject.rotationPitch();
    }

    public float getDistanceToEntity(Object entity) {
        return entityWrapperObject.getDistanceToEntity(entity);
    }

    public boolean isDead() {
        return entityWrapperObject.isDead();
    }

    public EntityWrapper getEntityWrapperObject() { return entityWrapperObject; }
}
