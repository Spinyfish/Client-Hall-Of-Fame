package net.minecraft.entity.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;

public abstract class EntityMinecartContainer extends EntityMinecart implements ILockableContainer {
    private ItemStack[] minecartContainerItems = new ItemStack[36];

    /**
     * When set to true, the minecart will drop all items when setDead() is called. When false (such as when travelling
     * dimensions) it preserves its contents.
     */
    private boolean dropContentsWhenDead = true;

    public EntityMinecartContainer(final World worldIn) {
        super(worldIn);
    }

    public EntityMinecartContainer(final World worldIn, final double p_i1717_2_, final double p_i1717_4_, final double p_i1717_6_) {
        super(worldIn, p_i1717_2_, p_i1717_4_, p_i1717_6_);
    }

    public void killMinecart(final DamageSource p_94095_1_) {
        super.killMinecart(p_94095_1_);

        if (this.worldObj.getGameRules().getGameRuleBooleanValue("doEntityDrops")) {
            InventoryHelper.func_180176_a(this.worldObj, this, this);
        }
    }

    /**
     * Returns the stack in the given slot.
     *
     * @param index The slot to retrieve from.
     */
    public ItemStack getStackInSlot(final int index) {
        return this.minecartContainerItems[index];
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     *
     * @param index The slot to remove from.
     * @param count The maximum amount of items to remove.
     */
    public ItemStack decrStackSize(final int index, final int count) {
        if (this.minecartContainerItems[index] != null) {
            if (this.minecartContainerItems[index].stackSize <= count) {
                final ItemStack itemstack1 = this.minecartContainerItems[index];
                this.minecartContainerItems[index] = null;
                return itemstack1;
            } else {
                final ItemStack itemstack = this.minecartContainerItems[index].splitStack(count);

                if (this.minecartContainerItems[index].stackSize == 0) {
                    this.minecartContainerItems[index] = null;
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    /**
     * Removes a stack from the given slot and returns it.
     *
     * @param index The slot to remove a stack from.
     */
    public ItemStack getStackInSlotOnClosing(final int index) {
        if (this.minecartContainerItems[index] != null) {
            final ItemStack itemstack = this.minecartContainerItems[index];
            this.minecartContainerItems[index] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(final int index, final ItemStack stack) {
        this.minecartContainerItems[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty() {
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(final EntityPlayer player) {
        return !this.isDead && player.getDistanceSqToEntity(this) <= 64.0D;
    }

    public void openInventory(final EntityPlayer player) {
    }

    public void closeInventory(final EntityPlayer player) {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(final int index, final ItemStack stack) {
        return true;
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getCommandSenderName() {
        return this.hasCustomName() ? this.getCustomNameTag() : "container.minecart";
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    public int getInventoryStackLimit() {
        return 64;
    }

    /**
     * Teleports the entity to another dimension. Params: Dimension number to teleport to
     */
    public void travelToDimension(final int dimensionId) {
        this.dropContentsWhenDead = false;
        super.travelToDimension(dimensionId);
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead() {
        if (this.dropContentsWhenDead) {
            InventoryHelper.func_180176_a(this.worldObj, this, this);
        }

        super.setDead();
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(final NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        final NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.minecartContainerItems.length; ++i) {
            if (this.minecartContainerItems[i] != null) {
                final NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                this.minecartContainerItems[i].writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        tagCompound.setTag("Items", nbttaglist);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(final NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        final NBTTagList nbttaglist = tagCompund.getTagList("Items", 10);
        this.minecartContainerItems = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            final NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            final int j = nbttagcompound.getByte("Slot") & 255;

            if (j >= 0 && j < this.minecartContainerItems.length) {
                this.minecartContainerItems[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
            }
        }
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(final EntityPlayer playerIn) {
        if (!this.worldObj.isRemote) {
            playerIn.displayGUIChest(this);
        }

        return true;
    }

    protected void applyDrag() {
        final int i = 15 - Container.calcRedstoneFromInventory(this);
        final float f = 0.98F + (float) i * 0.001F;
        this.motionX *= f;
        this.motionY *= 0.0D;
        this.motionZ *= f;
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
        return false;
    }

    public void setLockCode(final LockCode code) {
    }

    public LockCode getLockCode() {
        return LockCode.EMPTY_CODE;
    }

    public void clear() {
        for (int i = 0; i < this.minecartContainerItems.length; ++i) {
            this.minecartContainerItems[i] = null;
        }
    }
}
