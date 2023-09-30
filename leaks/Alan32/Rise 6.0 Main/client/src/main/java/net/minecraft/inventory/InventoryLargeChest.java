package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

public class InventoryLargeChest implements ILockableContainer {
    /**
     * Name of the chest.
     */
    private final String name;

    /**
     * Inventory object corresponding to double chest upper part
     */
    private final ILockableContainer upperChest;

    /**
     * Inventory object corresponding to double chest lower part
     */
    private final ILockableContainer lowerChest;

    public InventoryLargeChest(final String nameIn, ILockableContainer upperChestIn, ILockableContainer lowerChestIn) {
        this.name = nameIn;

        if (upperChestIn == null) {
            upperChestIn = lowerChestIn;
        }

        if (lowerChestIn == null) {
            lowerChestIn = upperChestIn;
        }

        this.upperChest = upperChestIn;
        this.lowerChest = lowerChestIn;

        if (upperChestIn.isLocked()) {
            lowerChestIn.setLockCode(upperChestIn.getLockCode());
        } else if (lowerChestIn.isLocked()) {
            upperChestIn.setLockCode(lowerChestIn.getLockCode());
        }
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory() {
        return this.upperChest.getSizeInventory() + this.lowerChest.getSizeInventory();
    }

    /**
     * Return whether the given inventory is part of this large chest.
     */
    public boolean isPartOfLargeChest(final IInventory inventoryIn) {
        return this.upperChest == inventoryIn || this.lowerChest == inventoryIn;
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getCommandSenderName() {
        return this.upperChest.hasCustomName() ? this.upperChest.getCommandSenderName() : (this.lowerChest.hasCustomName() ? this.lowerChest.getCommandSenderName() : this.name);
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName() {
        return this.upperChest.hasCustomName() || this.lowerChest.hasCustomName();
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    public IChatComponent getDisplayName() {
        return this.hasCustomName() ? new ChatComponentText(this.getCommandSenderName()) : new ChatComponentTranslation(this.getCommandSenderName(), new Object[0]);
    }

    /**
     * Returns the stack in the given slot.
     *
     * @param index The slot to retrieve from.
     */
    public ItemStack getStackInSlot(final int index) {
        return index >= this.upperChest.getSizeInventory() ? this.lowerChest.getStackInSlot(index - this.upperChest.getSizeInventory()) : this.upperChest.getStackInSlot(index);
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     *
     * @param index The slot to remove from.
     * @param count The maximum amount of items to remove.
     */
    public ItemStack decrStackSize(final int index, final int count) {
        return index >= this.upperChest.getSizeInventory() ? this.lowerChest.decrStackSize(index - this.upperChest.getSizeInventory(), count) : this.upperChest.decrStackSize(index, count);
    }

    /**
     * Removes a stack from the given slot and returns it.
     *
     * @param index The slot to remove a stack from.
     */
    public ItemStack getStackInSlotOnClosing(final int index) {
        return index >= this.upperChest.getSizeInventory() ? this.lowerChest.getStackInSlotOnClosing(index - this.upperChest.getSizeInventory()) : this.upperChest.getStackInSlotOnClosing(index);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(final int index, final ItemStack stack) {
        if (index >= this.upperChest.getSizeInventory()) {
            this.lowerChest.setInventorySlotContents(index - this.upperChest.getSizeInventory(), stack);
        } else {
            this.upperChest.setInventorySlotContents(index, stack);
        }
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    public int getInventoryStackLimit() {
        return this.upperChest.getInventoryStackLimit();
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty() {
        this.upperChest.markDirty();
        this.lowerChest.markDirty();
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(final EntityPlayer player) {
        return this.upperChest.isUseableByPlayer(player) && this.lowerChest.isUseableByPlayer(player);
    }

    public void openInventory(final EntityPlayer player) {
        this.upperChest.openInventory(player);
        this.lowerChest.openInventory(player);
    }

    public void closeInventory(final EntityPlayer player) {
        this.upperChest.closeInventory(player);
        this.lowerChest.closeInventory(player);
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(final int index, final ItemStack stack) {
        return true;
    }

    public int getField(final int id) {
        return 0;
    }

    public void setField(final int id, final int value) {
    }

    public int getFieldCount() {
        return 0;
    }

    public boolean isLocked() {
        return this.upperChest.isLocked() || this.lowerChest.isLocked();
    }

    public void setLockCode(final LockCode code) {
        this.upperChest.setLockCode(code);
        this.lowerChest.setLockCode(code);
    }

    public LockCode getLockCode() {
        return this.upperChest.getLockCode();
    }

    public String getGuiID() {
        return this.upperChest.getGuiID();
    }

    public Container createContainer(final InventoryPlayer playerInventory, final EntityPlayer playerIn) {
        return new ContainerChest(playerInventory, this, playerIn);
    }

    public void clear() {
        this.upperChest.clear();
        this.lowerChest.clear();
    }
}
