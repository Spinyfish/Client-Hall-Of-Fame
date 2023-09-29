package me.jinthium.straight.impl.modules.player.nofall;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.components.FallDistanceComponent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.player.NoFall;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.player.MovementUtil;

@ModeInfo(name = "Round", parent = NoFall.class)
public class RoundNoFall extends ModuleMode<NoFall> {

    @Callback
    EventCallback<PlayerUpdateEvent> onMotion = event -> {
        if (MovementUtil.canFall() && FallDistanceComponent.distance > 2.5) {
            event.setPosY(Math.round(event.getPosY()));
            event.setOnGround(true);
            FallDistanceComponent.distance = 0;
        }
    };
}
