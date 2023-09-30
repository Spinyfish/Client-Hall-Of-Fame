/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.awt.Color;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventRender3D;
import wtf.monsoon.impl.module.combat.Aura;
import wtf.monsoon.impl.module.movement.Flight;
import wtf.monsoon.impl.module.movement.Speed;

public class TargetStrafe
extends Module {
    public static int direction = 1;
    public static boolean canMove;
    public double movespeed2;
    public float currentYaw;
    public Timer timer = new Timer();
    private boolean strafing;
    public Setting<Shape> shape = new Setting<Shape>("Shape", Shape.CIRCLE).describedBy("Only speed");
    public Setting<Boolean> onlySpeed = new Setting<Boolean>("Only Speed", false).describedBy("Only speed");
    public Setting<Double> range = new Setting<Double>("Range", 2.0).minimum(0.1).maximum(6.0).incrementation(0.1).describedBy("Range");
    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        Aura aura = Wrapper.getModule(Aura.class);
        if (this.mc.gameSettings.keyBindJump.isKeyDown() && aura.isEnabled() && aura.getTarget() != null && !aura.getTarget().isDead) {
            if (aura.getTarget() == null) {
                return;
            }
            float[] rotations = this.getRotationsEntity(aura.getTarget());
            this.movespeed2 = this.getBaseMoveSpeed();
            if ((double)aura.getTarget().getDistanceToEntity(this.mc.thePlayer) < this.range.getValue()) {
                if (Wrapper.getModule(Flight.class).isEnabled()) {
                    return;
                }
                if (this.mc.gameSettings.keyBindRight.isPressed()) {
                    direction = -1;
                }
                if (this.mc.gameSettings.keyBindLeft.isPressed()) {
                    direction = 1;
                }
                canMove = true;
                if (!aura.getTarget().isDead) {
                    canMove = false;
                }
                if (this.onlySpeed.getValue().booleanValue()) {
                    if (Wrapper.getModule(Speed.class).isEnabled()) {
                        if (this.mc.gameSettings.keyBindRight.isPressed()) {
                            direction = -1;
                        }
                        if (this.mc.gameSettings.keyBindLeft.isPressed()) {
                            direction = 1;
                        }
                        canMove = true;
                        if (Wrapper.getModule(Speed.class).isEnabled() && direction != -1 && direction == 1) {
                            // empty if block
                        }
                    } else {
                        return;
                    }
                }
                if (this.mc.gameSettings.keyBindRight.isPressed()) {
                    direction = -1;
                }
                if (this.mc.gameSettings.keyBindLeft.isPressed()) {
                    direction = 1;
                }
                canMove = true;
                if (!Wrapper.getModule(Speed.class).isEnabled() || direction == -1 || direction == 1) {
                    // empty if block
                }
                if (canMove) {
                    this.strafe((EventMove)e, this.movespeed2, rotations[0], direction, 0.0);
                    this.currentYaw = rotations[0];
                } else {
                    this.currentYaw = this.mc.thePlayer.rotationYaw;
                }
            } else {
                if (this.mc.gameSettings.keyBindJump.isKeyDown() && (double)aura.getTarget().getDistanceToEntity(this.mc.thePlayer) < aura.getRange().getValue()) {
                    this.currentYaw = rotations[0];
                    this.strafe((EventMove)e, this.movespeed2, rotations[0], direction, 1.0);
                    this.setStrafing(true);
                } else {
                    this.currentYaw = this.mc.thePlayer.rotationYaw;
                    this.setStrafing(false);
                }
                canMove = false;
            }
        } else {
            this.setStrafing(false);
        }
    };
    @EventLink
    public final Listener<EventRender3D> eventRender3dListener = e -> {
        Aura aura = Wrapper.getModule(Aura.class);
        if (aura.getTarget() != null && (double)aura.getTarget().getDistanceToEntity(this.mc.thePlayer) < aura.getRange().getValue() && aura.isEnabled() && !aura.getTarget().isDead) {
            switch (this.shape.getValue()) {
                case CIRCLE: {
                    RenderUtil.drawCircle(aura.getTarget(), e.getPartialTicks(), this.range.getValue(), Color.WHITE, 2.0f, 1.0);
                    break;
                }
                case DECAGON: {
                    RenderUtil.drawCircle(aura.getTarget(), e.getPartialTicks(), this.range.getValue(), Color.BLACK, 3.7f, 9.0);
                    RenderUtil.drawCircle(aura.getTarget(), e.getPartialTicks(), this.range.getValue(), Color.WHITE, 1.0f, 9.0);
                }
            }
        }
    };
    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {};

    public TargetStrafe() {
        super("Target Strafe", "Automatically strafe your opponents.", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public double getBaseMoveSpeed() {
        return this.player.getSpeed();
    }

    public void strafe(EventMove event, double moveSpeed, float yaw, double strafe, double forward) {
        double fow = forward;
        double stra = strafe;
        float ya = yaw;
        if (fow != 0.0) {
            if (strafe > 0.0) {
                ya += (float)(fow > 0.0 ? -45 : 45);
            } else if (strafe < 0.0) {
                ya += (float)(fow > 0.0 ? 45 : -45);
            }
            stra = 0.0;
            if (fow > 0.0) {
                fow = 1.0;
            } else if (fow < 0.0) {
                fow = -1.0;
            }
        }
        if (stra > 1.0) {
            stra = 1.0;
        } else if (stra < 0.0) {
            stra = -1.0;
        }
        double mx = Math.cos(Math.toRadians(ya + 90.0f));
        double mz = Math.sin(Math.toRadians(ya + 90.0f));
        this.mc.thePlayer.motionX = fow * moveSpeed * mx + stra * moveSpeed * mz;
        event.setX(this.mc.thePlayer.motionX);
        this.mc.thePlayer.motionZ = fow * moveSpeed * mz - stra * moveSpeed * mx;
        event.setZ(this.mc.thePlayer.motionZ);
    }

    public float[] getRotationsEntity(EntityLivingBase entity) {
        return this.getRotations(entity.posX + TargetStrafe.randomNumber(0.03, -0.03), entity.posY + (double)entity.getEyeHeight() - 0.4 + TargetStrafe.randomNumber(0.07, -0.07), entity.posZ + TargetStrafe.randomNumber(0.03, -0.03));
    }

    public static double randomNumber(double max, double min) {
        return Math.random() * (max - min) + min;
    }

    public float[] getRotations(double posX, double posY, double posZ) {
        EntityPlayerSP player = this.mc.thePlayer;
        double x = posX - player.posX;
        double y = posY - player.posY + (double)((Entity)player).getEyeHeight();
        double z = posZ - player.posZ;
        double dist = Math.sqrt(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(y, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }

    public boolean isStrafing() {
        return this.strafing;
    }

    public void setStrafing(boolean strafing) {
        this.strafing = strafing;
    }

    static enum Shape {
        CIRCLE,
        DECAGON;

    }
}

