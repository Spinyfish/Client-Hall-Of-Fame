/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.EventUpdate;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.event.impl.player.EventCollideBlock;
import lodomir.dev.event.impl.player.EventMove;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.movement.Fly;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.ui.notification.Notification;
import lodomir.dev.ui.notification.NotificationManager;
import lodomir.dev.ui.notification.NotificationType;
import lodomir.dev.utils.math.TimeUtils;
import lodomir.dev.utils.player.MovementUtils;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

public class Speed
extends Module {
    private NumberSetting speed = new NumberSetting("Speed", 0.1, 10.0, 1.0, 0.1);
    private NumberSetting mctimer = new NumberSetting("Timer", 1.0, 2.0, 1.0, 0.1);
    private ModeSetting mode = new ModeSetting("Mode", "Bhop", "Bhop", "Y-Port", "Ground", "Vulcan", "Spartan", "Watchdog", "BlocksMC", "Verus", "AAC", "NCP", "NCP Low", "NCP Updated", "Strafe");
    private ModeSetting verusMode = new ModeSetting("Verus Mode", "Hop", "Hop", "Y-Port", "Low", "Dev");
    private BooleanSetting stop = new BooleanSetting("Disable on flag", true);
    private BooleanSetting strafe = new BooleanSetting("Strafe", true);
    public TimeUtils timer = new TimeUtils();
    private int level = 1;
    private double moveSpeed = 0.2873;
    private double lastDist;
    private boolean lastGround;
    private int timerDelay;
    private int offGroundTicks;
    private int ticks;
    public double movementSpeed;
    private double lastDistance;
    private boolean jumped;
    public boolean groundSpoof;
    private boolean bool;
    private int airMoves;

    public Speed() {
        super("Speed", 0, Category.MOVEMENT);
        this.addSettings(this.mode, this.verusMode, this.speed, this.strafe, this.mctimer, this.stop);
    }

    @Override
    public void onEnable() {
        this.lastDistance = 0.0;
        this.lastGround = Speed.mc.thePlayer.onGround;
        this.moveSpeed = 0.0;
        this.jumped = false;
        this.ticks = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (this.mode.isMode("NCP Updated")) {
            Speed.mc.thePlayer.speedInAir = 0.02f;
        }
        Speed.mc.timer.timerSpeed = 1.0f;
        super.onDisable();
    }

    @Override
    @Subscribe
    public void onGetPacket(EventGetPacket event) {
        if (this.stop.isEnabled() && event.getPacket() instanceof S08PacketPlayerPosLook) {
            this.setEnabled(false);
            NotificationManager.show(new Notification(NotificationType.WARNING, "Disabled Speed", "Disabled speed due to a teleport", 4));
        }
    }

    @Override
    @Subscribe
    public void onGuiUpdate(EventUpdate e) {
        if (!(this.mode.isMode("Bhop") || this.mode.isMode("Y-Port") || this.mode.isMode("Ground"))) {
            this.speed.setVisible(false);
        } else {
            this.speed.setVisible(true);
        }
        if (this.mode.isMode("NCP Updated")) {
            this.mctimer.setVisible(true);
        } else {
            this.mctimer.setVisible(false);
        }
        if (this.mode.isMode("Strafe")) {
            this.strafe.setVisible(true);
        } else {
            this.strafe.setVisible(false);
        }
        if (this.mode.isMode("Verus")) {
            this.verusMode.setVisible(true);
        } else {
            this.verusMode.setVisible(false);
        }
    }

    @Subscribe
    public void onUpdate(lodomir.dev.event.impl.game.EventUpdate e) {
        this.setSuffix(this.mode.getMode());
        if (this.mode.isMode("NCP Updated")) {
            Speed.mc.timer.timerSpeed = this.mctimer.getValueFloat();
        }
        switch (this.mode.getMode()) {
            case "Bhop": {
                this.bhop();
                break;
            }
            case "Y-Port": {
                this.yport();
                break;
            }
            case "Ground": {
                this.ground();
                break;
            }
            case "NCP": {
                this.ncp();
                break;
            }
            case "Strafe": {
                this.strafe();
            }
        }
    }

    private void ground() {
        if (Speed.mc.thePlayer.onGround && MovementUtils.isMoving()) {
            MovementUtils.setMotion(this.speed.getValueFloat());
        }
    }

    private final void bhop() {
        if (Speed.mc.thePlayer.onGround && MovementUtils.isMoving()) {
            Speed.mc.thePlayer.jump();
        }
        if (MovementUtils.isMoving()) {
            MovementUtils.strafe(this.speed.getValueFloat());
        }
    }

    private final void yport() {
        if (MovementUtils.isMoving()) {
            MovementUtils.strafe(this.speed.getValueFloat());
            MovementUtils.setMotion(this.speed.getValueFloat());
        }
    }

    @Override
    @Subscribe
    public void onBlockCollide(EventCollideBlock event) {
        if (Speed.mc.thePlayer.onGround && MovementUtils.isMoving()) {
            double x = event.getX();
            double y = event.getY();
            double z = event.getZ();
            if (y < Speed.mc.thePlayer.posY) {
                event.setCollisionBoundingBox(AxisAlignedBB.fromBounds(-20.0, -1.0, -20.0, 20.0, 1.0, 20.0).offset(x, y, z));
            }
        }
    }

    @Override
    @Subscribe
    public void onMove(EventMove event) {
        switch (this.mode.getMode()) {
            case "Y-Port": {
                if (Speed.mc.thePlayer.ticksExisted % 4 != 0 || !Speed.mc.thePlayer.onGround || !MovementUtils.isMoving()) break;
                event.setY(0.42);
                break;
            }
            case "NCP Low": {
                if (MovementUtils.isMoving() && Speed.mc.thePlayer.onGround) {
                    Speed.mc.thePlayer.motionY = 0.42;
                    if (Speed.mc.thePlayer.isPotionActive(Potion.moveSpeed) && Speed.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 == 2) {
                        Speed.mc.thePlayer.motionX *= (double)1.1f;
                        Speed.mc.thePlayer.motionZ *= (double)1.1f;
                        Speed.mc.thePlayer.speedInAir = 0.03f;
                    } else if (Speed.mc.thePlayer.isPotionActive(Potion.moveSpeed) && Speed.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 == 1) {
                        Speed.mc.thePlayer.motionX *= (double)1.1f;
                        Speed.mc.thePlayer.motionZ *= (double)1.1f;
                        Speed.mc.thePlayer.speedInAir = 0.025f;
                    } else {
                        Speed.mc.thePlayer.motionX *= (double)1.08f;
                        Speed.mc.thePlayer.motionZ *= (double)1.08f;
                    }
                }
                Speed.mc.thePlayer.motionY = -(Speed.mc.thePlayer.posY - Fly.roundToOnGround(Speed.mc.thePlayer.posY));
                break;
            }
            case "BlocksMC": {
                if (!Speed.mc.thePlayer.onGround || !MovementUtils.isMoving()) break;
                Speed.mc.thePlayer.jump();
                MovementUtils.strafe((float)(MovementUtils.getBaseMoveSpeed() * (Speed.mc.thePlayer.hurtTime != 0 ? 2.135 : 1.035)));
                break;
            }
            case "Verus": {
                if (!this.mode.isMode("Verus") || !this.verusMode.isMode("Low") || !MovementUtils.isMoving()) break;
                Speed.mc.gameSettings.keyBindJump.pressed = false;
                if (Speed.mc.thePlayer.onGround) {
                    Speed.mc.thePlayer.jump();
                    Speed.mc.thePlayer.motionY = 0.0;
                    MovementUtils.strafe(0.69f);
                    if (Speed.mc.thePlayer.ticksExisted % 5 == 0) {
                        event.setY(0.41999998688698);
                    }
                }
                MovementUtils.strafe();
                break;
            }
            case "Watchdog": {
                if (Speed.mc.thePlayer.onGround) {
                    this.lastGround = true;
                    if (!MovementUtils.isMoving()) break;
                    Speed.mc.thePlayer.motionY = 0.42;
                    event.setY(0.42);
                    double jumpSpeed = 0.2;
                    if (Speed.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                        jumpSpeed += 0.03 + (double)Speed.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.03;
                    }
                    double jumpSpeed2 = 0.23;
                    if (Speed.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                        jumpSpeed2 += 0.085 + (double)Speed.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.065;
                    }
                    MovementUtils.strafe((float)Math.max(MovementUtils.getBaseMoveSpeed() + jumpSpeed2, (double)MovementUtils.getSpeed() + jumpSpeed));
                    break;
                }
                if (this.lastGround) {
                    double speed = 1.0;
                    if (Speed.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                        speed += 0.04 + (double)Speed.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.04;
                    }
                    event.setX(Speed.mc.thePlayer.motionX *= speed);
                    event.setZ(Speed.mc.thePlayer.motionZ *= speed);
                    this.lastGround = false;
                }
                if ((double)Math.round(event.getY() * 1000.0) / 1000.0 == 0.165) {
                    Speed.mc.thePlayer.motionY = 0.0;
                    event.setY(0.0);
                }
                if (Speed.mc.thePlayer.isSprinting()) break;
                float f = MovementUtils.getPlayerDirection() * ((float)Math.PI / 180);
                event.setX(Speed.mc.thePlayer.motionX - (double)(MathHelper.sin(f) * 0.026f));
                event.setZ(Speed.mc.thePlayer.motionZ + (double)(MathHelper.cos(f) * 0.026f));
            }
        }
    }

    @Override
    @Subscribe
    public void onPreMotion(EventPreMotion event) {
        if (this.mode.isMode("Vulcan") && MovementUtils.isMoving()) {
            if (Speed.mc.thePlayer.onGround) {
                Speed.mc.thePlayer.jump();
                Speed.mc.thePlayer.motionY = 0.42;
                MovementUtils.strafe((float)((double)MovementUtils.getSpeed() * (Speed.mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.1 : 1.05)));
            } else if (!Speed.mc.thePlayer.onGround && Speed.mc.thePlayer.ticksExisted % 15 == 0) {
                Speed.mc.thePlayer.motionY = -0.42;
            }
        }
        if (this.mode.isMode("Spartan") && Speed.mc.gameSettings.keyBindForward.isKeyDown() && !Speed.mc.gameSettings.keyBindJump.isKeyDown()) {
            if (Speed.mc.thePlayer.onGround) {
                Speed.mc.thePlayer.jump();
                this.airMoves = 0;
            } else {
                Speed.mc.timer.timerSpeed = 1.08f;
                if (this.airMoves >= 3) {
                    Speed.mc.thePlayer.jumpMovementFactor = 0.0275f;
                }
                if (this.airMoves >= 4 && (double)(this.airMoves % 2) == 0.0) {
                    Speed.mc.thePlayer.motionY = (double)-0.32f - 0.009 * Math.random();
                    Speed.mc.thePlayer.jumpMovementFactor = 0.0238f;
                }
                ++this.airMoves;
            }
        }
        if (this.mode.isMode("Verus") && this.verusMode.isMode("Dev")) {
            if (MovementUtils.isMoving() && Speed.mc.thePlayer.onGround) {
                Speed.mc.thePlayer.setSprinting(true);
                Speed.mc.thePlayer.motionY = 0.42f;
            }
            MovementUtils.strafe((float)(MovementUtils.getBaseMoveSpeed() * (Speed.mc.thePlayer.hurtTime != 0 ? 2.135 : 1.035)));
        }
        if (this.mode.isMode("Verus") && this.verusMode.isMode("Hop") && MovementUtils.isMoving()) {
            double speedAmplifier = Speed.mc.thePlayer.isPotionActive(Potion.moveSpeed) ? (double)(Speed.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1) * 0.1 : 0.0;
            Speed.mc.gameSettings.keyBindJump.pressed = false;
            if (Speed.mc.thePlayer.onGround && Speed.mc.gameSettings.keyBindForward.isKeyDown()) {
                Speed.mc.thePlayer.jump();
                MovementUtils.strafe((float)(0.48 + speedAmplifier));
            } else if (Speed.mc.thePlayer.onGround) {
                Speed.mc.thePlayer.jump();
                MovementUtils.strafe((float)MovementUtils.getBaseMoveSpeed());
            }
            MovementUtils.strafe(MovementUtils.getSpeed());
        }
        if (this.mode.isMode("AAC")) {
            if (Speed.mc.thePlayer.isInWater()) {
                return;
            }
            if (Speed.mc.thePlayer.moveForward > 0.0f && Speed.mc.thePlayer.onGround) {
                Speed.mc.thePlayer.jump();
                Speed.mc.thePlayer.motionX *= 1.0708;
                Speed.mc.thePlayer.motionZ *= 1.0708;
            }
        }
        if (this.mode.isMode("NCP Updated")) {
            if (MovementUtils.isMoving()) {
                if (Speed.mc.thePlayer.onGround) {
                    Speed.mc.timer.timerSpeed = 2.0f;
                    if (Speed.mc.thePlayer.isPotionActive(Potion.moveSpeed) && Speed.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 == 2) {
                        Speed.mc.thePlayer.motionX *= (double)1.1f;
                        Speed.mc.thePlayer.motionZ *= (double)1.1f;
                        Speed.mc.thePlayer.speedInAir = 0.03f;
                    } else if (Speed.mc.thePlayer.isPotionActive(Potion.moveSpeed) && Speed.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 == 1) {
                        Speed.mc.thePlayer.motionX *= (double)1.1f;
                        Speed.mc.thePlayer.motionZ *= (double)1.1f;
                        Speed.mc.thePlayer.speedInAir = 0.025f;
                    } else {
                        Speed.mc.thePlayer.motionX *= (double)1.08f;
                        Speed.mc.thePlayer.motionZ *= (double)1.08f;
                    }
                    Speed.mc.thePlayer.jump();
                }
                MovementUtils.strafe();
            } else {
                MovementUtils.stop();
            }
        }
    }

    private final void ncp() {
        if (MovementUtils.isMoving()) {
            if (Speed.mc.thePlayer.onGround) {
                Speed.mc.thePlayer.jump();
                MovementUtils.strafe((float)((double)MovementUtils.getSpeed() + Math.random() / 100.0));
            } else {
                MovementUtils.strafe((float)((double)MovementUtils.getSpeed() * 1.0035));
            }
            Speed.mc.thePlayer.jumpMovementFactor = 0.02725f;
        }
        if (this.ticks < 20) {
            Speed.mc.timer.timerSpeed = 0.98f;
        }
        if (this.ticks == 20) {
            this.ticks = 0;
        }
    }

    private final void strafe() {
        if (MovementUtils.isMoving()) {
            if (Speed.mc.thePlayer.onGround) {
                Speed.mc.thePlayer.jump();
            }
            if (this.strafe.isEnabled()) {
                MovementUtils.strafe();
            }
        } else {
            MovementUtils.stop();
        }
    }
}

