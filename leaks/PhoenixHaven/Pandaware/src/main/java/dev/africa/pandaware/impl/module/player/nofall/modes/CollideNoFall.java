package dev.africa.pandaware.impl.module.player.nofall.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.CollisionEvent;
import dev.africa.pandaware.impl.module.movement.flight.FlightModule;
import dev.africa.pandaware.impl.module.player.nofall.NoFallModule;
import dev.africa.pandaware.utils.player.PlayerUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;

public class CollideNoFall extends ModuleMode<NoFallModule> {
    public CollideNoFall(String name, NoFallModule parent) {
        super(name, parent);
    }

    @EventHandler
    EventCallback<CollisionEvent> onCollision = event -> {
        if (mc.thePlayer != null && !mc.thePlayer.isSneaking() && mc.thePlayer.fallDistance >= 2.6 && this.getParent().canFall() &&
                (event.getBlock() instanceof BlockAir || event.getBlock() instanceof BlockLiquid)) {
            if (!Client.getInstance().getModuleManager().getByClass(FlightModule.class).getData().isEnabled()) {
                PlayerUtils.setCollisionGround(event);
            }
        }
    };
}