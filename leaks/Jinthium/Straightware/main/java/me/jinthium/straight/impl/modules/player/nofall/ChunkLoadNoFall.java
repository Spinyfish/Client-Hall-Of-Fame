package me.jinthium.straight.impl.modules.player.nofall;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.components.FallDistanceComponent;
import me.jinthium.straight.impl.event.game.TeleportEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.player.NoFall;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

@ModeInfo(name = "Chunk Load", parent = NoFall.class)
public class ChunkLoadNoFall extends ModuleMode<NoFall> {

    private boolean fakeUnloaded;

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if(!event.isPre())
            return;

        if (fakeUnloaded) {
            mc.thePlayer.motionY = 0.0D;

            event.setOnGround(false);
            event.setPosY(event.getPosY() - 0.098F);
            mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, event.getPosY(), mc.thePlayer.posZ);

            return;
        }

        if (mc.thePlayer.motionY > 0.0D || FallDistanceComponent.distance <= 3.0F) {
            return;
        }

        final Block nextBlock = MovementUtil.block(new BlockPos(
                event.getPosX(),
                event.getPosY() + mc.thePlayer.motionY,
                event.getPosZ()
        ));

        if (nextBlock.getMaterial().isSolid()) {
            FallDistanceComponent.distance = 0.0F;
            fakeUnloaded = true;
        }
    };

    @Callback
    final EventCallback<TeleportEvent> teleportEventEventCallback = event -> fakeUnloaded = false;
}
