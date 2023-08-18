package me.jinthium.straight.impl.event.movement;


import me.jinthium.straight.api.event.Event;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class PlayerMoveUpdateEvent extends Event {

    private float strafe, forward, friction, yaw, pitch;

    public PlayerMoveUpdateEvent(float strafe, float forward, float friction, float yaw, float pitch) {
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
        this.yaw = yaw;
        this.pitch = pitch;
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

    public void setSpeed(float speed){
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        player.motionX *= player.motionZ *= 0;
        setFriction(speed);
    }

    public void setSpeed(float speed, float motionMultiplier){
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        player.motionX *= motionMultiplier;
        player.motionZ *= motionMultiplier;
        setFriction(getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed);
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
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

    public void applyMotion(double speed, float strafeMotion) {
        float remainder = 1 - strafeMotion;
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        strafeMotion *= 0.98;
        if (player.onGround) {
            MovementUtil.setSpeed(speed);
        } else {
            player.motionX = player.motionX * strafeMotion;
            player.motionZ = player.motionZ * strafeMotion;
            friction = (float) speed * remainder;
        }
    }

}