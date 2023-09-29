package me.jinthium.straight.impl.modules.player.nofall;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.components.FallDistanceComponent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.player.NoFall;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.player.MovementUtil;

@ModeInfo(name = "Ground", parent = NoFall.class)
public class GroundNoFall extends ModuleMode<NoFall> {

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if (MovementUtil.canFall() && FallDistanceComponent.distance > 2 && mc.thePlayer.ticksExisted % 2 == 0) {
            event.setOnGround(true);
        }
    };
}
