package me.jinthium.straight.impl.modules.player.nofall;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.components.FallDistanceComponent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.player.NoFall;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.player.MovementUtil;

@ModeInfo(name = "Glitch", parent = NoFall.class)
public class GlitchNoFall extends ModuleMode<NoFall> {

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if (MovementUtil.canFall() && FallDistanceComponent.distance > 2.5 && MovementUtil.isOnGround(1)) {
            event.setOnGround(true);
            event.setPosY(event.getPosY() - 1f);
            FallDistanceComponent.distance = 0;
        }
    };
}
