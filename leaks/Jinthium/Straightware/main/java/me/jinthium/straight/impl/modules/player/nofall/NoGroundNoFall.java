package me.jinthium.straight.impl.modules.player.nofall;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.player.NoFall;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;

@ModeInfo(name = "No Ground", parent = NoFall.class)
public class NoGroundNoFall extends ModuleMode<NoFall> {

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> event.setOnGround(false);
}
