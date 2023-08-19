package cc.swift.module.impl.misc;

import cc.swift.events.UpdateEvent;
import cc.swift.module.Module;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

public class AutoToolModule extends Module {
    public AutoToolModule() {
        super("AutoTool", Module.Category.MISC);
    }

    private int oldSlot;
    private int tick;


    @Handler
    public final Listener<UpdateEvent> updateEventListener = e -> {
        if (mc.gameSettings.keyBindAttack.isKeyDown() && mc.thePlayer.capabilities.allowEdit &&
                mc.objectMouseOver.typeOfHit.equals(MovingObjectPosition.MovingObjectType.BLOCK)) {
            tick++;

            if (tick == 1) {
                oldSlot = mc.thePlayer.inventory.currentItem;
            }
            changeTool(mc.objectMouseOver.getBlockPos());
        } else if (tick > 0) {
            mc.thePlayer.inventory.currentItem = oldSlot;
            tick = 0;
        }
    };

    public void changeTool(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        int optimalSlot = findOptimalSlot(block);

        if (optimalSlot != -1 && (mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem) != mc.thePlayer.inventory.getStackInSlot(optimalSlot))) {
            mc.thePlayer.inventory.currentItem = optimalSlot;
        }
    }

    private int findOptimalSlot(Block block) {
        float maxStrength = 1.0F;
        int optimalSlot = -1;

        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            float strength = itemStack != null ? itemStack.getStrVsBlock(block) : 0;

            if (strength > maxStrength) {
                optimalSlot = i;
                maxStrength = strength;
            }
        }

        return optimalSlot;
    }
}
