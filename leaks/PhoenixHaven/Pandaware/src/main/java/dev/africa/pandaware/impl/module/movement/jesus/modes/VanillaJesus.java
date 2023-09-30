package dev.africa.pandaware.impl.module.movement.jesus.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.CollisionEvent;
import dev.africa.pandaware.impl.module.movement.jesus.JesusModule;
import dev.africa.pandaware.utils.player.PlayerUtils;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.AxisAlignedBB;

public class VanillaJesus extends ModuleMode<JesusModule> {
    public VanillaJesus(String name, JesusModule parent) {
        super(name, parent);
    }

    @EventHandler
    EventCallback<CollisionEvent> onCollide = event -> {
        if (event.getBlock() instanceof BlockLiquid && PlayerUtils.checkWithLiquid(0.3f)) {
            if (mc.thePlayer != null && !mc.thePlayer.isSneaking()) {
                event.setCollisionBox(new AxisAlignedBB(event.getBlockPos(), event.getBlockPos().add(1, 1, 1)));
            }
        }
    };
}
