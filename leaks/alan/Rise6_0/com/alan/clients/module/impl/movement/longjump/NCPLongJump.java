package com.alan.clients.module.impl.movement.longjump;

import com.alan.clients.module.impl.movement.LongJump;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.newevent.impl.other.TeleportEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.impl.NumberValue;
import com.alan.clients.value.Mode;

/**
 * @author Auth
 * @since 18/11/2021
 */

public class NCPLongJump extends Mode<LongJump> {

    private final NumberValue bunnyFriction = new NumberValue("Bunny Friction", this, 159, 59, 259, 1);
    private final NumberValue groundSpeed = new NumberValue("Ground Speed", this, 0.4, 0.1, 3, 0.1);
    private final NumberValue jumpSpeed = new NumberValue("Jump Speed", this, 1.4, 0, 3, 0.1);
    private final NumberValue glide = new NumberValue("Glide", this, 0, 0, 3, 0.5);
    private final NumberValue timer = new NumberValue("Timer", this, 1, 0.1, 10, 0.1);

    private boolean reset;
    private double speed;

    public NCPLongJump(String name, LongJump parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {

        final double base = MoveUtil.getAllowedHorizontalDistance();

        if (MoveUtil.isMoving()) {
            switch (InstanceAccess.mc.thePlayer.offGroundTicks) {
                case 0:
                    InstanceAccess.mc.thePlayer.motionY = MoveUtil.jumpBoostMotion(0.424F);
                    speed = groundSpeed.getValue().doubleValue();
                    break;

                case 1:
                    speed = jumpSpeed.getValue().doubleValue();
                    break;

                default:
                    speed -= speed / (bunnyFriction.getValue().floatValue() + 0.9F);
                    break;
            }

            InstanceAccess.mc.timer.timerSpeed = timer.getValue().floatValue();
            reset = false;
        } else if (!reset) {
            speed = MoveUtil.getAllowedHorizontalDistance();
            InstanceAccess.mc.timer.timerSpeed = 1;
            reset = true;
        }

        if (InstanceAccess.mc.thePlayer.fallDistance > 0) {
            InstanceAccess.mc.thePlayer.motionY += glide.getValue().floatValue() / 100;
        }

        if (InstanceAccess.mc.thePlayer.isCollidedHorizontally) {
            speed = MoveUtil.getAllowedHorizontalDistance();
        }

        event.setSpeed(Math.max(speed, base), Math.random() / 2000);
    };

    @EventLink()
    public final Listener<TeleportEvent> onTeleport = event -> {
        speed = 0;
    };

    @Override
    public void onDisable() {
        MoveUtil.stop();
        speed = 0;
    }
}