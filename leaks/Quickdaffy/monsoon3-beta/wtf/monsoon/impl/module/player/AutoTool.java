/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.combat.Aura;

public class AutoTool
extends Module {
    @EventLink
    private Listener<EventPreMotion> pre = e -> {
        if (Wrapper.getModule(Aura.class).isEnabled() && Wrapper.getModule(Aura.class).getTarget() != null) {
            float bestStr = 0.0f;
            int itemToUse = -1;
            for (int i = 0; i < 9; ++i) {
                ItemStack itemStack = this.mc.thePlayer.inventory.mainInventory[i];
                if (itemStack == null || !(itemStack.getItem() instanceof ItemSword)) continue;
                ItemSword item = (ItemSword)itemStack.getItem();
                if (!(item.attackDamage > bestStr)) continue;
                bestStr = item.attackDamage;
                itemToUse = i;
            }
            if (itemToUse != -1) {
                this.mc.thePlayer.inventory.currentItem = itemToUse;
            }
            return;
        }
        if (!this.mc.gameSettings.keyBindAttack.pressed || this.mc.objectMouseOver == null) {
            return;
        }
        BlockPos pos = this.mc.objectMouseOver.getBlockPos();
        if (pos == null) {
            return;
        }
        int itemToUse = this.getBestToolSlot(pos);
        if (itemToUse == -1) {
            return;
        }
        this.mc.thePlayer.inventory.currentItem = itemToUse;
    };
    @EventLink
    private Listener<EventPacket> packet = e -> {};

    public AutoTool() {
        super("Auto Tool", "Automatically switch to the correct tool when mining a block.", Category.PLAYER);
    }

    private int getBestToolSlot(BlockPos pos) {
        Block block = this.mc.theWorld.getBlockState(pos).getBlock();
        float bestStr = 1.0f;
        int itemTouse = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = this.mc.thePlayer.inventory.mainInventory[i];
            if (itemStack == null || !(itemStack.getStrVsBlock(block) > bestStr)) continue;
            bestStr = itemStack.getStrVsBlock(block);
            itemTouse = i;
        }
        return itemTouse;
    }
}

