/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui.inventory;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class CreativeCrafting
implements ICrafting {
    private final Minecraft mc;

    public CreativeCrafting(Minecraft mc) {
        this.mc = mc;
    }

    @Override
    public void updateCraftingInventory(Container containerToSend, List<ItemStack> itemsList) {
    }

    @Override
    public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
        this.mc.playerController.sendSlotPacket(stack, slotInd);
    }

    @Override
    public void sendProgressBarUpdate(Container containerIn, int varToUpdate, int newValue) {
    }

    @Override
    public void func_175173_a(Container p_175173_1_, IInventory p_175173_2_) {
    }
}

