package me.jinthium.straight.impl.modules.player.nofall;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.components.FallDistanceComponent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.player.NoFall;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

@ModeInfo(name = "Invalid", parent = NoFall.class)
public class InvalidNoFall extends ModuleMode<NoFall> {


    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {

        if (mc.thePlayer.motionY > 0 || !event.isPre()) {
            return;
        }

        float distance = FallDistanceComponent.distance;

        if (distance > 3) {
            final Block nextBlock = MovementUtil.block(new BlockPos(event.getPosX(), event.getPosY() + mc.thePlayer.motionY, event.getPosZ()));

            if (nextBlock.getMaterial().isSolid()) {
                event.setPosY(event.getPosY() - 999);
                distance = 0;
            }
        }

        FallDistanceComponent.distance = distance;
    };
}
