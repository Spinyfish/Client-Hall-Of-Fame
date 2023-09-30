package dev.africa.pandaware.impl.module.movement.flight.modes;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.module.movement.flight.FlightModule;
import dev.africa.pandaware.utils.player.MovementUtils;

public class ZonecraftFlight extends ModuleMode<FlightModule> {
    public ZonecraftFlight(String name, FlightModule parent) {
        super(name, parent);
    }

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            if (event.isOnGround()) {
                mc.thePlayer.motionY = 0.42f;
            } else {
                mc.thePlayer.motionY = 0;
            }
            MovementUtils.strafe(0.259);

            final double magicValue = 0.92160f / 8f;
            final double rounded = mc.thePlayer.posY - (mc.thePlayer.posY % magicValue);

            mc.thePlayer.setPosition(mc.thePlayer.posX, rounded, mc.thePlayer.posZ);
        }
    };
}
