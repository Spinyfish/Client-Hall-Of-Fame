package tech.dort.dortware.impl.events;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import tech.dort.dortware.api.event.Event;

public class StrafeEvent extends Event {

    private final State state;
    private double strafe;
    private double forward;
    private double friction;
    private double jumpHeight;
    private boolean silent;
    private float yaw;
    private float pitch;
    private double multi;

    public StrafeEvent(double strafe, double forward, double friction, float yaw, float pitch, State state) {
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
        this.yaw = yaw;
        this.pitch = pitch;
        this.state = state;
    }

    public StrafeEvent(float rotationYaw, double forward, double height) {
        this(0, forward, 0, rotationYaw, 0, State.JUMP);
        this.jumpHeight = height;
    }

    public double getStrafe() {
        return strafe;
    }

    public void setStrafe(double strafe) {
        this.strafe = strafe;
    }

    public double getForward() {
        return forward;
    }

    public void setForward(double forward) {
        this.forward = forward;
    }

    public double getFriction() {
        return friction;
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public State getState() {
        return state;
    }

    public double getJumpHeight() {
        return jumpHeight;
    }

    public void setJumpHeight(double jumpHeight) {
        this.jumpHeight = jumpHeight;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setHorizontalBoost(double multi) {
        this.multi = multi;
    }

    public double getHorizontalBoost() {
        return multi;
    }

    public void doJump(Entity entity) {
        entity.motionY = jumpHeight;

        if (entity.isSprinting()) {
            double var1 = entity.rotationYaw * 0.017453292F;
            entity.motionX -= Math.sin(var1) * multi;
            entity.motionZ += Math.cos(var1) * multi;
        }
    }

    public void removeMotion() {
        Minecraft.getMinecraft().thePlayer.motionX = 0;
        Minecraft.getMinecraft().thePlayer.motionZ = 0;
    }

    public enum State {
        JUMP, STRAFE
    }

}
