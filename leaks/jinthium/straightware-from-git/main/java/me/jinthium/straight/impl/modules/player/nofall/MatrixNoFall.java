package me.jinthium.straight.impl.modules.player.nofall;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.components.FallDistanceComponent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.player.NoFall;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import me.jinthium.straight.impl.utils.player.PlayerUtil;

@ModeInfo(name = "Matrix", parent = NoFall.class)
public class MatrixNoFall extends ModuleMode<NoFall> {

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if(event.isPre()) {
            float distance = FallDistanceComponent.distance;

            if (PlayerUtil.isBlockUnder()) {
                if (distance > 2) {
                    MovementUtil.strafe(0.19);
                }

                if (distance > 3 && MovementUtil.getSpeed() < 0.2) {
                    event.setOnGround(true);
                    distance = 0;
                }
            }

            FallDistanceComponent.distance = distance;
        }
    };
}
