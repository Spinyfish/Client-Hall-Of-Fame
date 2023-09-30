/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.util.obj;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.MathHelper;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.entity.MovementUtil;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.movement.Sprint;

public class MonsoonPlayerObject {
    private Minecraft mc = Minecraft.getMinecraft();
    private boolean sprintingServerSide;
    private boolean sneakingServerSide;
    private boolean invOpenServerSide;
    private boolean onGroundServerSide;
    private double posXServerSide;
    private double posYServerSide;
    private double posZServerSide;
    private float yawServerSide;
    private float pitchServerSide;
    @EventLink
    public Listener<EventPreMotion> eventPreMotionListener = e -> {};
    @EventLink
    public Listener<EventPacket> eventPacketListener = e -> {
        if (e.getDirection() == EventPacket.Direction.SEND) {
            Packet<INetHandlerPlayServer> packet;
            if (e.getPacket() instanceof C03PacketPlayer) {
                packet = (C03PacketPlayer)e.getPacket();
                this.setOnGroundServerSide(((C03PacketPlayer)packet).isOnGround());
                this.setPosXServerSide(((C03PacketPlayer)packet).getX());
                this.setPosYServerSide(((C03PacketPlayer)packet).getY());
                this.setPosZServerSide(((C03PacketPlayer)packet).getZ());
                this.setYawServerSide(((C03PacketPlayer)packet).getYaw());
                this.setPitchServerSide(((C03PacketPlayer)packet).getPitch());
            }
            if (e.getPacket() instanceof C0BPacketEntityAction) {
                packet = (C0BPacketEntityAction)e.getPacket();
                switch (((C0BPacketEntityAction)packet).getAction()) {
                    case START_SPRINTING: {
                        this.setSprintingServerSide(true);
                        break;
                    }
                    case STOP_SPRINTING: {
                        this.setSprintingServerSide(false);
                        break;
                    }
                    case START_SNEAKING: {
                        this.setSneakingServerSide(true);
                        break;
                    }
                    case STOP_SLEEPING: {
                        this.setSneakingServerSide(false);
                        break;
                    }
                    case OPEN_INVENTORY: {
                        this.setInvOpenServerSide(true);
                    }
                }
            }
            if (e.getPacket() instanceof C16PacketClientStatus) {
                packet = (C16PacketClientStatus)e.getPacket();
                switch (((C16PacketClientStatus)packet).getStatus()) {
                    case OPEN_INVENTORY_ACHIEVEMENT: {
                        this.setInvOpenServerSide(true);
                    }
                }
            }
            if (e.getPacket() instanceof C0DPacketCloseWindow && ((C0DPacketCloseWindow)(packet = (C0DPacketCloseWindow)e.getPacket())).getWindowId() == this.mc.thePlayer.inventoryContainer.windowId) {
                this.setInvOpenServerSide(false);
            }
        }
    };

    public double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (this.mc.thePlayer != null && this.mc.theWorld != null && this.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0 + 0.2 * (double)(this.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return baseSpeed;
    }

    public float getSpeed() {
        return (float)Math.sqrt(this.mc.thePlayer.motionX * this.mc.thePlayer.motionX + this.mc.thePlayer.motionZ * this.mc.thePlayer.motionZ);
    }

    public boolean isMoving() {
        return this.mc.thePlayer.movementInput.moveForward != 0.0f || this.mc.thePlayer.movementInput.moveStrafe != 0.0f;
    }

    public boolean isOnGround(float height) {
        return !this.mc.theWorld.getCollidingBoundingBoxes(this.mc.thePlayer, this.mc.thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
    }

    public void setSpeed(double speed) {
        float direction = (float)Math.toRadians(MovementUtil.getDirection());
        if (this.isMoving()) {
            this.mc.thePlayer.motionX = -Math.sin(direction) * speed;
            this.mc.thePlayer.motionZ = Math.cos(direction) * speed;
        } else {
            this.mc.thePlayer.motionZ = 0.0;
            this.mc.thePlayer.motionX = 0.0;
        }
    }

    public void setSpeed(double speed, float direction) {
        direction = (float)Math.toRadians(direction);
        if (this.isMoving()) {
            this.mc.thePlayer.motionX = -Math.sin(direction) * speed;
            this.mc.thePlayer.motionZ = Math.cos(direction) * speed;
        } else {
            this.mc.thePlayer.motionZ = 0.0;
            this.mc.thePlayer.motionX = 0.0;
        }
    }

    public void setSpeed(EventMove event, double speed) {
        float direction = (float)Math.toRadians(MovementUtil.getDirection());
        if (this.isMoving()) {
            this.mc.thePlayer.motionX = -Math.sin(direction) * speed;
            event.setX(this.mc.thePlayer.motionX);
            this.mc.thePlayer.motionZ = Math.cos(direction) * speed;
            event.setZ(this.mc.thePlayer.motionZ);
        } else {
            this.mc.thePlayer.motionX = 0.0;
            event.setX(0.0);
            this.mc.thePlayer.motionZ = 0.0;
            event.setZ(0.0);
        }
    }

    public void setSpeed(EventMove event, double speed, float direction) {
        direction = (float)Math.toRadians(direction);
        if (this.isMoving()) {
            this.mc.thePlayer.motionX = -Math.sin(direction) * speed;
            event.setX(this.mc.thePlayer.motionX);
            this.mc.thePlayer.motionZ = Math.cos(direction) * speed;
            event.setZ(this.mc.thePlayer.motionZ);
        } else {
            this.mc.thePlayer.motionX = 0.0;
            event.setX(0.0);
            this.mc.thePlayer.motionZ = 0.0;
            event.setZ(0.0);
        }
    }

    public void setSpeedWithCorrection(EventMove event, double speed, double lastMotionX, double lastMotionZ) {
        this.setSpeedWithCorrection(event, speed, lastMotionX, lastMotionZ, 0.5);
    }

    public void setSpeedWithCorrection(EventMove event, double speed, double lastMotionX, double lastMotionZ, double modifier) {
        float direction = (float)Math.toRadians(MovementUtil.getDirection());
        if (this.isMoving()) {
            this.mc.thePlayer.motionX = -Math.sin(direction) * speed;
            event.setX(this.mc.thePlayer.motionX);
            this.mc.thePlayer.motionZ = Math.cos(direction) * speed;
            event.setZ(this.mc.thePlayer.motionZ);
        } else {
            this.mc.thePlayer.motionX = 0.0;
            event.setX(0.0);
            this.mc.thePlayer.motionZ = 0.0;
            event.setZ(0.0);
        }
        if (event.getX() > 0.0 && event.getX() > lastMotionX) {
            this.mc.thePlayer.motionX = lastMotionX + (event.getX() - lastMotionX) * modifier;
            event.setX(this.mc.thePlayer.motionX);
        } else if (event.getX() < 0.0 && event.getX() < lastMotionX) {
            this.mc.thePlayer.motionX = lastMotionX - (lastMotionX - event.getX()) * modifier;
            event.setX(this.mc.thePlayer.motionX);
        }
        if (event.getZ() > 0.0 && event.getZ() > lastMotionZ) {
            this.mc.thePlayer.motionZ = lastMotionZ + (event.getZ() - lastMotionZ) * modifier;
            event.setZ(this.mc.thePlayer.motionZ);
        } else if (event.getZ() < 0.0 && event.getZ() < lastMotionZ) {
            this.mc.thePlayer.motionZ = lastMotionZ - (lastMotionZ - event.getZ()) * modifier;
            event.setZ(this.mc.thePlayer.motionZ);
        }
    }

    public int getJumpBoostModifier() {
        PotionEffect effect = this.mc.thePlayer.getActivePotionEffect(Potion.jump);
        if (effect != null) {
            return effect.getAmplifier() + 1;
        }
        return 0;
    }

    public double getJumpHeight(double baseJumpHeight) {
        if (this.mc.thePlayer.isInWater() || this.mc.thePlayer.isInLava()) {
            return 0.13499999955296516;
        }
        if (this.mc.thePlayer.isPotionActive(Potion.jump)) {
            return baseJumpHeight + (double)(((float)this.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1.0f) * 0.1f);
        }
        return baseJumpHeight;
    }

    public float getJumpHeight(float baseJumpHeight) {
        if (this.mc.thePlayer.isInWater() || this.mc.thePlayer.isInLava()) {
            return 0.135f;
        }
        if (this.mc.thePlayer.isPotionActive(Potion.jump)) {
            return baseJumpHeight + ((float)this.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1.0f) * 0.1f;
        }
        return baseJumpHeight;
    }

    public void strafe() {
        this.setSpeed(this.getSpeed());
    }

    public void strafe(EventMove event) {
        this.setSpeed(event, (double)this.getSpeed());
    }

    public boolean isOnGround() {
        return this.mc.thePlayer.onGround;
    }

    public void setOnGround(boolean onGround) {
        this.mc.thePlayer.onGround = onGround;
    }

    public void jump(float motionY) {
        this.mc.thePlayer.motionY = this.getJumpHeight(motionY);
        if (this.mc.thePlayer.isPotionActive(Potion.jump)) {
            this.mc.thePlayer.motionY += (double)((float)(this.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f);
        }
        if (this.mc.thePlayer.isSprinting()) {
            Sprint sprint = Wrapper.getModule(Sprint.class);
            float f = (sprint.isEnabled() && sprint.omni.getValue() != false ? MovementUtil.getDirection() : this.mc.thePlayer.rotationYaw) * ((float)Math.PI / 180);
            this.mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * 0.2f);
            this.mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.2f);
        }
        this.mc.thePlayer.isAirBorne = true;
        this.mc.thePlayer.triggerAchievement(StatList.jumpStat);
        if (this.mc.thePlayer.isSprinting()) {
            this.mc.thePlayer.addExhaustion(0.8f);
        } else {
            this.mc.thePlayer.addExhaustion(0.2f);
        }
    }

    public void jump() {
        this.jump(this.getJumpHeight(0.42f));
    }

    public void jump(EventMove eventMove, float motionY) {
        this.mc.thePlayer.motionY = this.getJumpHeight(motionY);
        eventMove.setY(this.mc.thePlayer.motionY);
        if (this.mc.thePlayer.isPotionActive(Potion.jump)) {
            eventMove.setY(this.mc.thePlayer.motionY += (double)((float)(this.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f));
        }
        if (this.mc.thePlayer.isSprinting()) {
            Sprint sprint = Wrapper.getModule(Sprint.class);
            float f = (sprint.isEnabled() && sprint.omni.getValue() != false ? MovementUtil.getDirection() : this.mc.thePlayer.rotationYaw) * ((float)Math.PI / 180);
            eventMove.setX(this.mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * 0.2f));
            eventMove.setZ(this.mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.2f));
        }
        this.mc.thePlayer.isAirBorne = true;
        this.mc.thePlayer.triggerAchievement(StatList.jumpStat);
        if (this.mc.thePlayer.isSprinting()) {
            this.mc.thePlayer.addExhaustion(0.8f);
        } else {
            this.mc.thePlayer.addExhaustion(0.2f);
        }
    }

    public void jump(EventMove eventMove) {
        this.jump(eventMove, this.getJumpHeight(0.42f));
    }

    public Minecraft getMc() {
        return this.mc;
    }

    public boolean isSprintingServerSide() {
        return this.sprintingServerSide;
    }

    public boolean isSneakingServerSide() {
        return this.sneakingServerSide;
    }

    public boolean isInvOpenServerSide() {
        return this.invOpenServerSide;
    }

    public boolean isOnGroundServerSide() {
        return this.onGroundServerSide;
    }

    public double getPosXServerSide() {
        return this.posXServerSide;
    }

    public double getPosYServerSide() {
        return this.posYServerSide;
    }

    public double getPosZServerSide() {
        return this.posZServerSide;
    }

    public float getYawServerSide() {
        return this.yawServerSide;
    }

    public float getPitchServerSide() {
        return this.pitchServerSide;
    }

    public Listener<EventPreMotion> getEventPreMotionListener() {
        return this.eventPreMotionListener;
    }

    public Listener<EventPacket> getEventPacketListener() {
        return this.eventPacketListener;
    }

    public void setMc(Minecraft mc) {
        this.mc = mc;
    }

    public void setSprintingServerSide(boolean sprintingServerSide) {
        this.sprintingServerSide = sprintingServerSide;
    }

    public void setSneakingServerSide(boolean sneakingServerSide) {
        this.sneakingServerSide = sneakingServerSide;
    }

    public void setInvOpenServerSide(boolean invOpenServerSide) {
        this.invOpenServerSide = invOpenServerSide;
    }

    public void setOnGroundServerSide(boolean onGroundServerSide) {
        this.onGroundServerSide = onGroundServerSide;
    }

    public void setPosXServerSide(double posXServerSide) {
        this.posXServerSide = posXServerSide;
    }

    public void setPosYServerSide(double posYServerSide) {
        this.posYServerSide = posYServerSide;
    }

    public void setPosZServerSide(double posZServerSide) {
        this.posZServerSide = posZServerSide;
    }

    public void setYawServerSide(float yawServerSide) {
        this.yawServerSide = yawServerSide;
    }

    public void setPitchServerSide(float pitchServerSide) {
        this.pitchServerSide = pitchServerSide;
    }

    public void setEventPreMotionListener(Listener<EventPreMotion> eventPreMotionListener) {
        this.eventPreMotionListener = eventPreMotionListener;
    }

    public void setEventPacketListener(Listener<EventPacket> eventPacketListener) {
        this.eventPacketListener = eventPacketListener;
    }
}

