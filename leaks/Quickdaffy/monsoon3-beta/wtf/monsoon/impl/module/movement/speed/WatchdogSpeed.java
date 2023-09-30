/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.movement.speed;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.block.BlockSlab;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;

public class WatchdogSpeed
extends ModeProcessor {
    private final Setting<String> wdOptions = new Setting<String>("Watchdog Options", "Watchdog Options").describedBy("Watchdog options.");
    public final Setting<WatchdogMode> watchdogMode = new Setting<WatchdogMode>("Watchdog Mode", WatchdogMode.HOP).describedBy("How to control speed on Hypixel").childOf(this.wdOptions);
    private final Setting<Double> speedWatchdog = new Setting<Double>("Speed", 1.7).minimum(1.0).maximum(2.0).incrementation(0.05).describedBy("The speed you will go.").visibleWhen(() -> this.watchdogMode.getValue() == WatchdogMode.HOP).childOf(this.wdOptions);
    private final Setting<Boolean> safeStrafe = new Setting<Boolean>("Safe Strafe", false).describedBy("Whether to enable safe strafe.").visibleWhen(() -> this.watchdogMode.getValue() == WatchdogMode.HOP).childOf(this.wdOptions);
    private final Setting<Double> strafeModifier = new Setting<Double>("Strafe Modifier", 0.5).minimum(0.1).maximum(1.0).incrementation(0.05).describedBy("The amount to modify strafe.").visibleWhen(() -> this.watchdogMode.getValue() == WatchdogMode.HOP && this.safeStrafe.getValue() != false || this.watchdogMode.getValue() == WatchdogMode.HOP_SMOOTH).childOf(this.wdOptions);
    private final Setting<Double> speedNoStrafe = new Setting<Double>("Speed (nostrafe)", 1.95).minimum(1.5).maximum(2.25).incrementation(0.05).describedBy("The speed you will go.").visibleWhen(() -> this.watchdogMode.getValue() == WatchdogMode.NO_STRAFE || this.watchdogMode.getValue() == WatchdogMode.DORT).childOf(this.wdOptions);
    private final Setting<Boolean> strafeOnDamage = new Setting<Boolean>("Strafe on Damage", true).describedBy("Strafe on damage").visibleWhen(() -> this.watchdogMode.getValue() == WatchdogMode.NO_STRAFE).childOf(this.wdOptions);
    private final Setting<Integer> strafeTickOption = new Setting<Integer>("Strafe Ticks", 15).minimum(1).maximum(18).incrementation(1).describedBy("Ticks to strafe for on damage").visibleWhen(() -> this.watchdogMode.getValue() == WatchdogMode.NO_STRAFE && this.strafeOnDamage.isVisible()).childOf(this.wdOptions);
    private double speed;
    private double lastDist;
    private int strafeTicks;
    private boolean prevOnGround;
    private boolean canStrafe;
    private double lastMotionX;
    private double lastMotionZ;
    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        switch (this.watchdogMode.getValue()) {
            case GROUND: {
                if (this.player.isOnGround() && this.mc.thePlayer.ticksExisted % 4 != 0 && !(this.mc.theWorld.getBlockState(this.mc.thePlayer.getPosition().add(0, -1, 0)).getBlock() instanceof BlockSlab)) {
                    this.mc.getTimer().timerSpeed = 4.0f;
                    break;
                }
                this.mc.getTimer().timerSpeed = 1.0f;
                break;
            }
            case HOP: 
            case NO_STRAFE: 
            case DORT: {
                double xDist = this.mc.thePlayer.posX - this.mc.thePlayer.prevPosX;
                double zDist = this.mc.thePlayer.posZ - this.mc.thePlayer.prevPosZ;
                this.lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
                ++this.strafeTicks;
            }
        }
    };
    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        switch (this.watchdogMode.getValue()) {
            case HOP: {
                if (!this.player.isMoving()) break;
                this.speed = this.player.getBaseMoveSpeed();
                if (this.mc.thePlayer.onGround && !this.prevOnGround) {
                    this.prevOnGround = true;
                    e.setY(0.41999998688698);
                    this.mc.thePlayer.motionY = 0.42;
                    this.speed *= this.speedWatchdog.getValue().doubleValue();
                } else if (this.prevOnGround) {
                    double difference = 0.76 * (this.lastDist - this.player.getBaseMoveSpeed());
                    this.speed = this.lastDist - difference;
                    this.prevOnGround = false;
                } else {
                    this.speed = this.lastDist - this.lastDist / 159.0;
                }
                if (this.mc.thePlayer.hurtTime > 0) {
                    this.speed = Math.hypot(this.mc.thePlayer.motionX, this.mc.thePlayer.motionZ) + (double)0.0245f;
                }
                if (this.safeStrafe.getValue().booleanValue() && !this.player.isOnGround()) {
                    this.player.setSpeedWithCorrection((EventMove)e, Math.max((double)this.player.getSpeed(), this.speed), this.lastMotionX, this.lastMotionZ, this.strafeModifier.getValue());
                } else {
                    this.player.setSpeed((EventMove)e, Math.max((double)this.player.getSpeed(), this.speed));
                }
                this.lastMotionX = e.getX();
                this.lastMotionZ = e.getZ();
                break;
            }
            case NO_STRAFE: {
                boolean bl = this.canStrafe = this.strafeTicks <= this.strafeTickOption.getValue() && this.strafeOnDamage.getValue() != false;
                if (!this.player.isMoving()) break;
                this.speed = this.player.getBaseMoveSpeed();
                if (this.mc.thePlayer.onGround) {
                    e.setY(0.41999998688698);
                    this.mc.thePlayer.motionY = 0.42;
                    this.speed = !this.canStrafe ? (this.speed *= this.speedNoStrafe.getValue().doubleValue()) : (this.speed *= 1.5);
                    this.prevOnGround = this.canStrafe;
                } else if (this.prevOnGround) {
                    double difference = 0.76 * (this.lastDist - this.player.getBaseMoveSpeed());
                    this.speed = this.lastDist - difference;
                    this.prevOnGround = false;
                } else {
                    this.speed = this.lastDist - this.lastDist / 159.0;
                }
                if (this.mc.thePlayer.hurtTime == 9) {
                    this.speed += 0.1;
                }
                if (!this.player.isOnGround() && !this.canStrafe) break;
                this.player.setSpeed((EventMove)e, Math.max(this.player.getBaseMoveSpeed(), this.speed));
                break;
            }
            case HOP_SMOOTH: {
                if (this.player.isMoving()) {
                    if (this.player.isOnGround()) {
                        this.prevOnGround = true;
                        if (this.player.isMoving()) {
                            e.setY(0.41999998688698);
                            this.mc.thePlayer.motionY = 0.42;
                            this.speed *= 0.91;
                            this.speed += 0.2 + (double)this.mc.thePlayer.getAIMoveSpeed();
                        }
                    } else if (this.prevOnGround) {
                        this.speed *= 0.54;
                        this.speed += 0.026;
                        this.prevOnGround = false;
                    } else {
                        this.speed *= 0.91;
                        this.speed += 0.025 + (this.player.getBaseMoveSpeed() - 0.2873) * 0.08;
                    }
                    if (this.mc.thePlayer.hurtTime == 9) {
                        this.speed += 0.1;
                    }
                    if (this.mc.thePlayer.fallDistance < 1.0f) {
                        this.player.setSpeedWithCorrection((EventMove)e, this.speed, this.lastMotionX, this.lastMotionZ, this.strafeModifier.getValue());
                    } else {
                        this.speed = this.player.getSpeed();
                    }
                }
                this.lastMotionX = e.getX();
                this.lastMotionZ = e.getZ();
                break;
            }
            case DORT: {
                if (!this.player.isMoving()) break;
                if (this.player.isOnGround()) {
                    this.speed = this.player.getBaseMoveSpeed() * this.speedNoStrafe.getValue();
                    this.player.setSpeed((EventMove)e, Math.max(this.speed, (double)this.player.getSpeed()));
                    this.mc.thePlayer.motionY = 0.42f;
                    e.setY(0.42f);
                }
                if (this.mc.thePlayer.motionY == 0.08307781780646721) {
                    this.mc.thePlayer.motionY = -0.08307781780646721;
                }
                if (this.mc.thePlayer.hurtTime <= 0) break;
            }
        }
    };
    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        if (e.getPacket() instanceof S06PacketUpdateHealth && this.mc.thePlayer.hurtTime > 0) {
            PlayerUtil.sendClientMessage("Set strafe ticks to 0");
            this.strafeTicks = 0;
        }
    };

    public WatchdogSpeed(Module parentModule) {
        super(parentModule);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.lastMotionX = this.mc.thePlayer.motionX;
        this.lastMotionZ = this.mc.thePlayer.motionZ;
        this.canStrafe = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.mc.getTimer().timerSpeed = 1.0f;
        this.mc.thePlayer.jumpMovementFactor = 0.02f;
    }

    @Override
    public Setting[] getModeSettings() {
        return new Setting[]{this.wdOptions};
    }

    private static enum WatchdogMode {
        HOP,
        HOP_SMOOTH,
        DORT,
        GROUND,
        NO_STRAFE;

    }
}

