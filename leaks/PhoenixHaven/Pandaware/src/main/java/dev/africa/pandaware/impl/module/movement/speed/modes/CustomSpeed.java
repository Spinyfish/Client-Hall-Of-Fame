package dev.africa.pandaware.impl.module.movement.speed.modes;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.movement.speed.SpeedModule;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.utils.player.MovementUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;

public class CustomSpeed extends ModuleMode<SpeedModule> {
    private final BooleanSetting useFallMotion = new BooleanSetting("Apply Fall Motion", false);
    private final BooleanSetting multiply = new BooleanSetting("Multiply Speed", false);
    private final BooleanSetting useFriction = new BooleanSetting("Follow Friction", false, () -> !this.multiply.getValue());
    private final NumberSetting groundSpeed = new NumberSetting("Ground Speed", 10, 0, 0.5, 0.01);
    private final NumberSetting airSpeed = new NumberSetting("Air Speed", 10, 0, 0.5, 0.01,
            () -> !(this.useFriction.getValue()));
    private final NumberSetting jumpMotion = new NumberSetting("Jump Motion", 0.42, 0, 0.42, 0.01);
    private final NumberSetting fallMotion = new NumberSetting("Fall Motion", 2, 0, 0.3, 0.1, this.useFallMotion::getValue);
    private final NumberSetting fallMotionTicks = new NumberSetting("Fall on Air Tick", 10, 1, 5, 1, this.useFallMotion::getValue);
    private final BooleanSetting useTimer = new BooleanSetting("Use Timer", false);
    private final NumberSetting timerSpeed = new NumberSetting("Timer", 2, 0.01, 1, 0.01, this.useTimer::getValue);

    private double lastDistance;
    private double moveSpeed;
    private boolean jumped;

    public CustomSpeed(String name, SpeedModule parent) {
        super(name, parent);

        this.registerSettings(
                this.groundSpeed,
                this.airSpeed,
                this.jumpMotion,
                this.useFallMotion,
                this.fallMotion,
                this.fallMotionTicks,
                this.useTimer,
                this.timerSpeed,
                this.useFriction,
                this.multiply
        );
    }

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            lastDistance = MovementUtils.getLastDistance();
        }
    };

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if (MovementUtils.isMoving()) mc.timer.timerSpeed = this.timerSpeed.getValue().floatValue();
        else mc.timer.timerSpeed = 1f;
        if (PlayerUtils.isMathGround() && this.multiply.getValue() && MovementUtils.isMoving()) {
            mc.thePlayer.motionX = mc.thePlayer.motionX * this.groundSpeed.getValue().floatValue();
            mc.thePlayer.motionZ = mc.thePlayer.motionZ * this.groundSpeed.getValue().floatValue();
            event.y = mc.thePlayer.motionY = jumpMotion.getValue().floatValue();
        } else if (PlayerUtils.isMathGround() && MovementUtils.isMoving()) {
            moveSpeed = groundSpeed.getValue().floatValue();
            event.y = mc.thePlayer.motionY = jumpMotion.getValue().floatValue();
            jumped = true;
        } else if (!mc.thePlayer.onGround && this.multiply.getValue()) {
            mc.thePlayer.motionX = mc.thePlayer.motionX * this.airSpeed.getValue().floatValue();
            mc.thePlayer.motionZ = mc.thePlayer.motionZ * this.airSpeed.getValue().floatValue();
        } else if (jumped && this.useFriction.getValue()) {
            moveSpeed = lastDistance - 0.66F * (lastDistance - MovementUtils.getBaseMoveSpeed());
            jumped = false;
        } else if (!mc.thePlayer.onGround && !this.useFriction.getValue()) {
            moveSpeed = this.airSpeed.getValue().floatValue();
        } else {
            moveSpeed = lastDistance * 0.91;
        }
        if (this.useFallMotion.getValue()) {
            if (mc.thePlayer.getAirTicks() == this.fallMotionTicks.getValue().intValue()) {
                event.y = mc.thePlayer.motionY -= this.fallMotion.getValue().floatValue();
            }
        }
        if (!this.multiply.getValue()) {
            MovementUtils.strafe(event, moveSpeed);
        }
    };

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1f;
        lastDistance = 0;
        moveSpeed = 0;
        jumped = false;
    }
}
