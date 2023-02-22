package com.alan.clients.module.impl.movement.speed;

import com.alan.clients.module.impl.movement.Speed;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.input.MoveInputEvent;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.Mode;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.NumberValue;

public final class StrafeSpeed extends Mode<Speed> {

    private final BooleanValue hurtBoost = new BooleanValue("Hurt Boost", this, false);
    private final NumberValue boostSpeed = new NumberValue("Boost Speed", this, 1, 0.1, 9.5, 0.1);

    public StrafeSpeed(String name, Speed parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {

        if (!MoveUtil.isMoving()) {
            MoveUtil.stop();
            return;
        }

        if (mc.thePlayer.onGround) {
            mc.thePlayer.jump();
        }

        if (hurtBoost.getValue() && mc.thePlayer.hurtTime == 9) {
            MoveUtil.strafe(boostSpeed.getValue().doubleValue());
        }

        MoveUtil.strafe();
    };

    @EventLink()
    public final Listener<MoveInputEvent> onMove = event -> {
        event.setJump(false);
    };
}
