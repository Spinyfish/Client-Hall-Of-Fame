/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IWorldNameable;

public interface IInventory
extends IWorldNameable {
    public int getSizeInventory();

    public ItemStack getStackInSlot(int var1);

    public ItemStack decrStackSize(int var1, int var2);

    public ItemStack removeStackFromSlot(int var1);

    public void setInventorySlotContents(int var1, ItemStack var2);

    public int getInventoryStackLimit();

    public void markDirty();

    public boolean isUseableByPlayer(EntityPlayer var1);

    public void openInventory(EntityPlayer var1);

    public void closeInventory(EntityPlayer var1);

    public boolean isItemValidForSlot(int var1, ItemStack var2);

    public int getField(int var1);

    public void setField(int var1, int var2);

    public int getFieldCount();

    public void clear();
}

