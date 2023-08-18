package me.jinthium.straight.impl.modules.combat.criticals;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.combat.Criticals;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;

@ModeInfo(name = "Watchdog", parent = Criticals.class)
public class WatchdogCrits extends ModuleMode<Criticals> {

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event ->  {
        if(event.isPre()){
            if (mc.thePlayer.ticksSinceVelocity <= 18 && mc.thePlayer.fallDistance < 1.3) {
                event.setOnGround(false);
            }
        }
    };
}
