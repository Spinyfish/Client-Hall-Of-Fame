package dev.tenacity.module.impl.movement.speeds.impl;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.player.input.MoveInputEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.event.impl.player.movement.correction.JumpEvent;
import dev.tenacity.event.impl.player.movement.correction.StrafeEvent;
import dev.tenacity.module.impl.combat.KillAura;
import dev.tenacity.module.impl.exploit.Breaker;
import dev.tenacity.module.impl.movement.Scaffold;
import dev.tenacity.module.impl.movement.speeds.SpeedMode;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.player.RotationUtils;

public class PolarSpeed extends SpeedMode {

    public PolarSpeed() {
        super("Polar");
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        // Code removed. Made by Liticane.
    }

}
