package dev.africa.pandaware.impl.module.player;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.CollisionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.utils.player.MovementUtils;
import net.minecraft.util.AxisAlignedBB;

@ModuleInfo(name = "Phase", category = Category.PLAYER)
public class PhaseModule extends Module {
    @EventHandler
    EventCallback<CollisionEvent> onTouchGrass = event -> {
        if (mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.isSneaking() && mc.thePlayer.isCollidedHorizontally) {
            event.setCollisionBox(new AxisAlignedBB(-100, -2, -100, 100, 1, 100)
                    .offset(event.getBlockPos().getX(), event.getBlockPos().getY() - 3, event.getBlockPos().getZ()));
        }
    };

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if (mc.thePlayer.isSneaking() && MovementUtils.isMoving()) {
            MovementUtils.strafe(event, MovementUtils.getBaseMoveSpeed());
        }
    };
}
