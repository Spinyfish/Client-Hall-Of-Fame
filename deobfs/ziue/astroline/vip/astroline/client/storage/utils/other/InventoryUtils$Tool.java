/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package vip.astroline.client.storage.utils.other;

import net.minecraft.item.ItemStack;

private static class InventoryUtils.Tool {
    private final int slot;
    private final double efficiency;
    private final ItemStack stack;

    public InventoryUtils.Tool(int slot, double efficiency, ItemStack stack) {
        this.slot = slot;
        this.efficiency = efficiency;
        this.stack = stack;
    }

    public int getSlot() {
        return this.slot;
    }

    public double getEfficiency() {
        return this.efficiency;
    }

    public ItemStack getStack() {
        return this.stack;
    }
}
