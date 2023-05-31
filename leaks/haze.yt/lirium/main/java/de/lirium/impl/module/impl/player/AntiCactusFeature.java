package de.lirium.impl.module.impl.player;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.impl.events.CollideEvent;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;

@ModuleFeature.Info(name = "Anti Cactus", description = "Prevents you from getting hurt by cactus", category = ModuleFeature.Category.PLAYER)
public class AntiCactusFeature extends ModuleFeature {

    @EventHandler
    public final Listener<CollideEvent> collideEvent = e -> {
        if (e.block == Blocks.CACTUS) {
            e.boundingBox = Block.FULL_BLOCK_AABB;
        }
    };
}