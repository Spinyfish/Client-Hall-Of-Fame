package io.github.raze.modules.collection.player;

import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPosition;

public class AutoTool extends BaseModule {

    public AutoTool() {
        super("AutoTool", "Equips the best tool automatically.", ModuleCategory.PLAYER);
    }

    @SubscribeEvent
    private void onMotion(EventMotion event) {

        if (!mc.gameSettings.keyBindAttack.isKeyDown())
            return;

        BlockPosition position = mc.objectMouseOver.getBlockPosition();
        Block block = mc.theWorld.getBlockState(position).getBlock();

        float best = 1F;
        int slot = 1000;

        for (int index = 0; index < 9; index += 1) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(index);

            if (itemStack == null)
                continue;

            float speed = itemStack.getStrVsBlock(block);

            if (speed > best) {
                best = speed;
                slot = index;
            }
        }

        if (slot == 1000)
            return;

        mc.thePlayer.inventory.currentItem = slot;
    }
    
}