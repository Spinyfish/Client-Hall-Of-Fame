package com.alan.clients.module.impl.movement.flight.deprecated;

import com.alan.clients.module.impl.movement.Flight;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.util.player.DamageUtil;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.Mode;

public class ACRFlight extends Mode<Flight> {

    private int offGroundTicks;

    public ACRFlight(String name, Flight parent) {
        super(name, parent);
    }

    @Override
    public void onEnable() {
        DamageUtil.damagePlayer(DamageUtil.DamageType.POSITION, 3.42F, 1, false, false);
    }

    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {
        MoveUtil.strafe(0.06);
        if (mc.thePlayer.ticksExisted % 3 == 0) {
            mc.thePlayer.motionY = 0.40444491418477213;
            offGroundTicks = 0;
        }

        if (offGroundTicks == 1) {
            MoveUtil.strafe(0.36);
            mc.thePlayer.motionY = 0.33319999363422365;
        }
    };

}
