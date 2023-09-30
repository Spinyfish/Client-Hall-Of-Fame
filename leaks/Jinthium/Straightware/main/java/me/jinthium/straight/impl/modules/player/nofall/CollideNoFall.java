package me.jinthium.straight.impl.modules.player.nofall;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.components.FallDistanceComponent;
import me.jinthium.straight.impl.event.movement.CollisionEvent;
import me.jinthium.straight.impl.modules.movement.Flight;
import me.jinthium.straight.impl.modules.player.NoFall;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import me.jinthium.straight.impl.utils.player.PlayerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;

@ModeInfo(name = "Collide", parent = NoFall.class)
public class CollideNoFall extends ModuleMode<NoFall> {

    @Callback
    final EventCallback<CollisionEvent> onCollision = event -> {
        if (mc.thePlayer != null && !mc.thePlayer.isSneaking() && FallDistanceComponent.distance >= 2.6 && MovementUtil.canFall() &&
                (event.getBlock() instanceof BlockAir || event.getBlock() instanceof BlockLiquid)) {
            if (!Client.INSTANCE.getModuleManager().getModule(Flight.class).isEnabled()) {
                PlayerUtil.setCollisionGround(event);
            }
        }
    };
}
