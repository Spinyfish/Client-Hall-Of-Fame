/**
 * @project hakarware
 * @author CodeMan
 * @at 24.07.23, 19:20
 */

package cc.swift.module.impl.movement;

import cc.swift.Swift;
import cc.swift.events.EventState;
import cc.swift.events.MovementInputEvent;
import cc.swift.events.UpdateEvent;
import cc.swift.events.UpdateWalkingPlayerEvent;
import cc.swift.module.Module;
import cc.swift.util.ChatUtil;
import cc.swift.util.player.MovementUtil;
import cc.swift.value.Value;
import cc.swift.value.impl.BooleanValue;
import cc.swift.value.impl.DoubleValue;
import cc.swift.value.impl.ModeValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

public final class SpeedModule extends Module {

    public final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.values());
    public final DoubleValue speed = new DoubleValue("Speed", 1.0, 0.1, 10.0, 0.1).setDependency(() -> this.mode.getValue() == Mode.VANILLA);

    public final BooleanValue groundStrafe = new BooleanValue("GroundStrafe", true).setDependency(() -> this.mode.getValue() == Mode.STRAFE);

    // Hypixel Values
    public final BooleanValue hypixelYDown = new BooleanValue("Lowhop on Hit", false).setDependency(() -> this.mode.getValue() == Mode.HYPIXEL);
    public final DoubleValue hypYDownAmt = new DoubleValue("Down Y Motion", 0D, 0, 0.1, 0.005).setDependency(() -> (mode.getValue() == Mode.HYPIXEL && hypixelYDown.getValue()));

    // Custom Speed Values
    // I know that there is already a speed mode, but with this you need to fine tune it and too high values means its hard to get what you want
    public final DoubleValue groundMotion = new DoubleValue("Speed", 0.9, 0.1, 2.5, 0.025).setDependency(() -> this.mode.getValue() == Mode.CUSTOM);
    public final DoubleValue groundFriction = new DoubleValue("Friction", 0.57, 0.3, 1.25, 0.01).setDependency(() -> this.mode.getValue() == Mode.CUSTOM);
    public final DoubleValue jumpMotion = new DoubleValue("Jump Motion", 0.42, 0.01, 1.5, 0.01).setDependency(() -> this.mode.getValue() == Mode.CUSTOM);
    public final DoubleValue yDown = new DoubleValue("Down Y Motion", 0d, 0, 1, 0.005).setDependency(() -> this.mode.getValue() == Mode.CUSTOM);
    public final ModeValue<MotionType> motionType = new ModeValue<>("Motion Type", MotionType.values()).setDependency(() -> this.mode.getValue() == Mode.CUSTOM);
    private double speedValInsaneInsanity = 0.0F;
    public final DoubleValue timerBoost = new DoubleValue("Timer Boost", 1.0, 1.0, 5.0, 0.1);
    private boolean isYDown, wasOnGround;
    private int ticks;

    public SpeedModule() {
        super("Speed", Category.MOVEMENT);
        this.registerValues(this.mode, this.speed, this.groundStrafe, this.groundMotion, this.groundFriction, this.jumpMotion, this.yDown, this.motionType, this.hypixelYDown, this.hypYDownAmt, timerBoost);
    }

    @Handler
    public final Listener<UpdateWalkingPlayerEvent> updateEventListener = event -> {
        if(event.getState() != EventState.PRE) return;
        switch (this.mode.getValue()) {
            case HYPIXEL:
                if (MovementUtil.isMoving()) {
                    if (MovementUtil.isOnGround()) {
                        mc.thePlayer.jump();

                        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                            double[] speed = new double[]{0.52, 0.585, 0.645};
                            MovementUtil.setSpeed(speed[Math.min(mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier(), speed.length - 1)] + Math.random() / 1000);
                        } else {
                            MovementUtil.setSpeed(0.475 + Math.random() / 100);
                        }

                        wasOnGround = true;
                    } else if (wasOnGround) {
                        wasOnGround = false;

                        MovementUtil.setSpeed(MovementUtil.getSpeed());
                    }
                    if(!MovementUtil.isOnGround() && mc.thePlayer.hurtTime == 9 && hypixelYDown.getValue() && mc.thePlayer.motionY > hypYDownAmt.getValue()){
                        mc.thePlayer.motionY = -hypYDownAmt.getValue();
                    }
                }
                break;
            case STRAFE:
                if (MovementUtil.isMoving()) {
                    if (MovementUtil.isOnGround()) {
                        mc.thePlayer.jump();
                    }
                    if (MovementUtil.isOnGround() || !this.groundStrafe.getValue()) {
                        MovementUtil.setSpeed(MovementUtil.getSpeed());
                    }
                }
                break;
            case LEGIT_JUMP:
                if (MovementUtil.isMoving() && MovementUtil.isOnGround()) { // when holding space is too hard
                    mc.thePlayer.jump();
                }
                break;
            case VANILLA:
                if (MovementUtil.isMoving() && MovementUtil.isOnGround()) {
                    mc.thePlayer.jump();
                }
                MovementUtil.setSpeed(MovementUtil.isMoving() ? this.speed.getValue() : 0);
                break;
            case BMC:
                if (MovementUtil.isMoving() && MovementUtil.isOnGround()) {
                    mc.thePlayer.jump();
                    speedValInsaneInsanity = this.speed.getValue();
                }
                speedValInsaneInsanity = MovementUtil.isMoving() ? Math.max(speedValInsaneInsanity * 0.98F, MovementUtil.getBaseMoveSpeed()) : 0;
                MovementUtil.setSpeed(speedValInsaneInsanity);
                break;
            case CUSTOM:
                if (MovementUtil.isMoving() && mc.thePlayer.onGround) {
                    Swift.INSTANCE.getPlayer().setGroundFriction(groundFriction.getValue().floatValue());
                    mc.thePlayer.jump();
                    mc.thePlayer.motionY = jumpMotion.getValue();
                    if (mc.thePlayer.isPotionActive(Potion.jump)) {
                        mc.thePlayer.motionY += (float) (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
                    }

                    isYDown = false;
                    double speedValue = MovementUtil.getBaseMoveSpeed() * groundMotion.getValue();

                    if(motionType.getValue() == MotionType.ADD)
                        MovementUtil.addSpeed(speedValue);
                    else
                        MovementUtil.setSpeed(speedValue);

                }
                else{
                    Swift.INSTANCE.getPlayer().setGroundFriction(Float.NaN);
                }
                if (yDown.getValue() > 0 && !isYDown && mc.thePlayer.motionY < 0.1) {
                    mc.thePlayer.motionY = -yDown.getValue();
                    isYDown = true;
                }
                break;

            case VULCAN:
                if(MovementUtil.isMoving()){
                    if (mc.thePlayer.onGround) {
                        MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * (mc.thePlayer.isPotionActive(1) ? 1.46 : 1.71));
                        mc.thePlayer.motionY = 0.005;
                        event.setY(event.getY() + mc.thePlayer.motionY);
                        ticks = 0;
                    } else if (++ticks == 1) {
                        MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * (mc.thePlayer.isPotionActive(1) ? 1.11 : 1.04));
                    }
                        new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING);
                }
                break;
        }
        mc.timer.timerSpeed = timerBoost.getValue().floatValue();

    };

    @Handler
    public final Listener<MovementInputEvent> movementInputEventListener = event -> {
        if (MovementUtil.isMoving())
            event.setJump(false);
    };

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.timer.timerSpeed = 1;
        ticks = 0;
    }

    enum Mode {
        VANILLA, LEGIT_JUMP, STRAFE, HYPIXEL, VULCAN, CUSTOM, BMC
    }

    enum MotionType {
        ADD,
        SET;
    }

}
