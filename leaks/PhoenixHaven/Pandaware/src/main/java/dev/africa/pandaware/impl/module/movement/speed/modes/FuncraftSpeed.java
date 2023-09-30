package dev.africa.pandaware.impl.module.movement.speed.modes;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.movement.speed.SpeedModule;
import dev.africa.pandaware.utils.player.MovementUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;

public class FuncraftSpeed extends ModuleMode<SpeedModule> {
    public FuncraftSpeed(String name, SpeedModule parent) {
        super(name, parent);
    }

    private int stage;
    private double moveSpeed;
    private double lastDistance;

    @Override
    public void onEnable() {
        this.stage = (mc.thePlayer.onGround ? 0 : 5);
        this.moveSpeed = MovementUtils.getBaseMoveSpeed();
        this.lastDistance = this.moveSpeed;
    }

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            this.lastDistance = MovementUtils.getLastDistance();
        }
    };

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if (MovementUtils.isMoving() && !PlayerUtils.onLiquid()) {
            if (mc.thePlayer.onGround && this.stage > 1) {
                this.stage = 0;
            }

            switch (this.stage) {
                case 0:
                    mc.timer.timerSpeed = 1.6F;

                    this.moveSpeed = MovementUtils.getBaseMoveSpeed() * 1.6;
                    break;
                case 1:
                    mc.timer.timerSpeed = 1.1F;

                    double motion = 0.41F;
                    motion += PlayerUtils.getJumpBoostMotion();
                    event.y = mc.thePlayer.motionY = motion;

                    this.moveSpeed *= 2.040;
                    break;
                case 2:
                    this.moveSpeed = this.lastDistance - (0.66 * (this.lastDistance
                            - MovementUtils.getBaseMoveSpeed()));
                    break;

                default:
                    if (mc.thePlayer.getAirTicks() == 5) {
                        event.y = mc.thePlayer.motionY = -0.02;
                    }

                    mc.timer.timerSpeed = 1f;
                    this.moveSpeed = this.lastDistance - this.lastDistance / 148.5;
                    break;
            }

            this.moveSpeed = Math.max(this.moveSpeed, MovementUtils.getBaseMoveSpeed());
            MovementUtils.strafe(event, this.moveSpeed);

            this.stage++;
        } else {
            mc.timer.timerSpeed = 1F;
        }
    };
}
