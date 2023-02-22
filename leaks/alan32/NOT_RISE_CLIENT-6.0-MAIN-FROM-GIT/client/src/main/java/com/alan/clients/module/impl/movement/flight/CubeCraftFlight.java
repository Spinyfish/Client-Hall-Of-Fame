package com.alan.clients.module.impl.movement.flight;

import com.alan.clients.Client;
import com.alan.clients.module.impl.movement.Flight;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.input.MoveInputEvent;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.Mode;
import com.alan.clients.value.impl.NumberValue;

/**
 * @author Auth
 * @since 18/11/2021
 */

public class CubeCraftFlight extends Mode<Flight>  {

    private final NumberValue speed = new NumberValue("Speed", this, 1, 0.1, 9.5, 0.1);

    public CubeCraftFlight(String name, Flight parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {

        final float speed = this.speed.getValue().floatValue();

        event.setSpeed(speed);
    };

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        final float speed = this.speed.getValue().floatValue();

        InstanceAccess.mc.thePlayer.motionY = -1E-10D
                + (InstanceAccess.mc.gameSettings.keyBindJump.isKeyDown() ? speed : 0.0D)
                - (InstanceAccess.mc.gameSettings.keyBindSneak.isKeyDown() ? speed : 0.0D);


        boolean playerNearby = !Client.INSTANCE.getTargetManager().getTargets(12).isEmpty() ||
                InstanceAccess.mc.currentScreen != null;

        if (InstanceAccess.mc.thePlayer.getDistance(InstanceAccess.mc.thePlayer.lastReportedPosX, InstanceAccess.mc.thePlayer.lastReportedPosY, InstanceAccess.mc.thePlayer.lastReportedPosZ)
                <= (playerNearby ? 5 : 10) - speed - 0.15 && InstanceAccess.mc.thePlayer.swingProgressInt != 3) {
            event.setCancelled(true);
        }
    };


    @EventLink()
    public final Listener<MoveInputEvent> onMove = event -> {
        event.setSneak(false);
    };

    @Override
    public void onDisable() {
        MoveUtil.stop();
    }
}