package club.pulsive.impl.module.impl.player;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.impl.event.network.PacketEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

import java.util.List;

@ModuleInfo(name = "Auto Tool", renderName = "Auto Tool", aliases = "AutoTool", description = "Automatically equips items.", category = Category.PLAYER)

public class AutoTool extends Module {
    @EventHandler
    private final Listener<PacketEvent> packetEventListener = event -> {
        switch(event.getEventState()) {
            case SENDING:
                if (event.getPacket() instanceof C07PacketPlayerDigging) {
                    C07PacketPlayerDigging packetPlayerDigging = (C07PacketPlayerDigging) event.getPacket();
                    if ((packetPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
                        if ((mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && !mc.thePlayer.capabilities.isCreativeMode)) {
                            BlockPos blockPosHit = packetPlayerDigging.getPosition();
                            mc.thePlayer.inventory.currentItem = getBestTool(blockPosHit);
                            mc.playerController.updateController();
                        }
                    }
                }
                break;
        }
    };
    private int getBestTool(BlockPos pos) {
        final Block block = mc.theWorld.getBlockState(pos).getBlock();
        int slot = 0;
        float dmg = 0.1F;
        for (int index = 36; index < 45; index++) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer
                    .getSlot(index).getStack();
            if (itemStack != null
                    && block != null
                    && itemStack.getItem().getStrVsBlock(itemStack, block) > dmg) {
                slot = index - 36;
                dmg = itemStack.getItem().getStrVsBlock(itemStack, block);
            }
        }
        if (dmg > 0.1F) {
            return slot;
        }
        return mc.thePlayer.inventory.currentItem;
    }
}
