package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class Slot {
    /**
     * The index of the slot in the inventory.
     */
    private final int slotIndex;

    /**
     * The inventory we want to extract a slot from.
     */
    public final IInventory inventory;

    /**
     * the id of the slot(also the index in the inventory arraylist)
     */
    public int slotNumber;

    /**
     * display position of the inventory slot on the screen x axis
     */
    public int xDisplayPosition;

    /**
     * display position of the inventory slot on the screen y axis
     */
    public int yDisplayPosition;

    public Slot(final IInventory inventoryIn, final int index, final int xPosition, final int yPosition) {
        this.inventory = inventoryIn;
        this.slotIndex = index;
        this.xDisplayPosition = xPosition;
        this.yDisplayPosition = yPosition;
    }

    /**
     * if par2 has more items than par1, onCrafting(item,countIncrease) is called
     */
    public void onSlotChange(final ItemStack p_75220_1_, final ItemStack p_75220_2_) {
        if (p_75220_1_ != null && p_75220_2_ != null) {
            if (p_75220_1_.getItem() == p_75220_2_.getItem()) {
                final int i = p_75220_2_.stackSize - p_75220_1_.stackSize;

                if (i > 0) {
                    this.onCrafting(p_75220_1_, i);
                }
            }
        }
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    protected void onCrafting(final ItemStack stack, final int amount) {
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void onCrafting(final ItemStack stack) {
    }

    public void onPickupFromSlot(final EntityPlayer playerIn, final ItemStack stack) {
        this.onSlotChanged();
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(final ItemStack stack) {
        return true;
    }

    /**
     * Helper fnct to get the stack in the slot.
     */
    public ItemStack getStack() {
        return this.inventory.getStackInSlot(this.slotIndex);
    }

    /**
     * Returns if this slot contains a stack.
     */
    public boolean getHasStack() {
        return this.getStack() != null;
    }

    /**
     * Helper method to put a stack in the slot.
     */
    public void putStack(final ItemStack stack) {
        this.inventory.setInventorySlotContents(this.slotIndex, stack);
        this.onSlotChanged();
    }

    /**
     * Called when the stack in a Slot changes
     */
    public void onSlotChanged() {
        this.inventory.markDirty();
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    public int getSlotStackLimit() {
        return this.inventory.getInventoryStackLimit();
    }

    public int getItemStackLimit(final ItemStack stack) {
        return this.getSlotStackLimit();
    }

    public String getSlotTexture() {
        return null;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize(final int amount) {
        return this.inventory.decrStackSize(this.slotIndex, amount);
    }

    /**
     * returns true if the slot exists in the given inventory and location
     */
    public boolean isHere(final IInventory inv, final int slotIn) {
        return inv == this.inventory && slotIn == this.slotIndex;
    }

    /**
     * Return whether this slot's stack can be taken from this slot.
     */
    public boolean canTakeStack(final EntityPlayer playerIn) {
        return true;
    }

    /**
     * Actualy only call when we want to render the white square effect over the slots. Return always True, except for
     * the armor slot of the Donkey/Mule (we can't interact with the Undead and Skeleton horses)
     */
    public boolean canBeHovered() {
        return true;
    }
}
