package cc.swift.module.impl.player;

import cc.swift.events.BlockAABBEvent;
import cc.swift.module.Module;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.block.BlockCactus;
import net.minecraft.util.AxisAlignedBB;

public class AntiCactusModule extends Module {

    public AntiCactusModule() {
        super("AntiCactus", Category.PLAYER);
    }

    @Handler
    public final Listener<BlockAABBEvent> blockAABBEventListener = event -> {
        if(event.getBlock() instanceof BlockCactus) {
            event.setBoundingBox(new AxisAlignedBB(event.getBlockPos(), event.getBlockPos().add(1, 1, 1)));
        }
    };
}
