package com.alan.clients.module.impl.player;

import com.alan.clients.api.Rise;
import com.alan.clients.component.impl.player.SlotComponent;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.newevent.impl.other.BlockDamageEvent;
import com.alan.clients.util.player.SlotUtil;
import net.minecraft.util.BlockPos;

/**
 * @author Alan (made good code)
 * @since 24/06/2023
 */

@Rise
@ModuleInfo(name = "module.player.autotool.name", description = "module.player.autotool.description", category = Category.PLAYER)
public class AutoTool extends Module {

    private int slot, lastSlot = -1;
    private int blockBreak;
    private BlockPos blockPos;

    @EventLink(Priorities.VERY_HIGH)
    public final Listener<BlockDamageEvent> onBlockDamage = event -> {
        blockBreak = 3;
        blockPos = event.getBlockPos();
    };

    @EventLink()
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        switch (mc.objectMouseOver.typeOfHit) {
            case BLOCK:
                if (blockPos != null && blockBreak > 0) {
                    slot = SlotUtil.findTool(blockPos);
                } else {
                    slot = -1;
                }
                break;

            case ENTITY:
                slot = SlotUtil.findSword();
                break;

            default:
                slot = -1;
                break;
        }

        if (lastSlot != -1) {
            SlotComponent.setSlot(lastSlot);
        } else if (slot != -1) {
            SlotComponent.setSlot(slot);
        }

        lastSlot = slot;
        blockBreak--;
    };

}