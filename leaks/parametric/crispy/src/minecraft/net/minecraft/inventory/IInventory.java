package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IWorldNameable;

public interface IInventory extends IWorldNameable
{
    /**
     * Returns the number of slots in the inventory.
     */
    int getSizeInventory();

    /**
     * Returns the stack in the given slot.
     *  
     * @param index The slot to retrieve from.
     */
    ItemStack getStackInSlot(int var1);

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     *  
     * @param index The slot to remove from.
     * @param count The maximum amount of items to remove.
     */
    ItemStack decrStackSize(int var1, int var2);

    /**
     * Removes a stack from the given slot and returns it.
     *  
     * @param index The slot to remove a stack from.
     */
    ItemStack getStackInSlotOnClosing(int var1);

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    void setInventorySlotContents(int var1, ItemStack var2);

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    int getInventoryStackLimit();

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    void markDirty();

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    boolean isUseableByPlayer(EntityPlayer var1);

    void openInventory(EntityPlayer var1);

    void closeInventory(EntityPlayer var1);

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    boolean isItemValidForSlot(int var1, ItemStack var2);

    int getField(int var1);

    void setField(int var1, int var2);

    int getFieldCount();

    void clear();
}
