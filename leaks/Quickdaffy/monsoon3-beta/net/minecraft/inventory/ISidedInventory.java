/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface ISidedInventory
extends IInventory {
    public int[] getSlotsForFace(EnumFacing var1);

    public boolean canInsertItem(int var1, ItemStack var2, EnumFacing var3);

    public boolean canExtractItem(int var1, ItemStack var2, EnumFacing var3);
}

