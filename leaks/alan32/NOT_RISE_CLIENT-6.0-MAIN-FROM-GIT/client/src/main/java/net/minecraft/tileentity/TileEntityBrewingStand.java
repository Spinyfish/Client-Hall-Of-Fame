package net.minecraft.tileentity;

import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import java.util.Arrays;
import java.util.List;

public class TileEntityBrewingStand extends TileEntityLockable implements ITickable, ISidedInventory {
    /**
     * an array of the input slot indices
     */
    private static final int[] inputSlots = new int[]{3};

    /**
     * an array of the output slot indices
     */
    private static final int[] outputSlots = new int[]{0, 1, 2};

    /**
     * The ItemStacks currently placed in the slots of the brewing stand
     */
    private ItemStack[] brewingItemStacks = new ItemStack[4];
    private int brewTime;

    /**
     * an integer with each bit specifying whether that slot of the stand contains a potion
     */
    private boolean[] filledSlots;

    /**
     * used to check if the current ingredient has been removed from the brewing stand during brewing
     */
    private Item ingredientID;
    private String customName;

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getCommandSenderName() {
        return this.hasCustomName() ? this.customName : "container.brewing";
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName() {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setName(final String name) {
        this.customName = name;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory() {
        return this.brewingItemStacks.length;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update() {
        if (this.brewTime > 0) {
            --this.brewTime;

            if (this.brewTime == 0) {
                this.brewPotions();
                this.markDirty();
            } else if (!this.canBrew()) {
                this.brewTime = 0;
                this.markDirty();
            } else if (this.ingredientID != this.brewingItemStacks[3].getItem()) {
                this.brewTime = 0;
                this.markDirty();
            }
        } else if (this.canBrew()) {
            this.brewTime = 400;
            this.ingredientID = this.brewingItemStacks[3].getItem();
        }

        if (!this.worldObj.isRemote) {
            final boolean[] aboolean = this.func_174902_m();

            if (!Arrays.equals(aboolean, this.filledSlots)) {
                this.filledSlots = aboolean;
                IBlockState iblockstate = this.worldObj.getBlockState(this.getPos());

                if (!(iblockstate.getBlock() instanceof BlockBrewingStand)) {
                    return;
                }

                for (int i = 0; i < BlockBrewingStand.HAS_BOTTLE.length; ++i) {
                    iblockstate = iblockstate.withProperty(BlockBrewingStand.HAS_BOTTLE[i], Boolean.valueOf(aboolean[i]));
                }

                this.worldObj.setBlockState(this.pos, iblockstate, 2);
            }
        }
    }

    private boolean canBrew() {
        if (this.brewingItemStacks[3] != null && this.brewingItemStacks[3].stackSize > 0) {
            final ItemStack itemstack = this.brewingItemStacks[3];

            if (!itemstack.getItem().isPotionIngredient(itemstack)) {
                return false;
            } else {
                boolean flag = false;

                for (int i = 0; i < 3; ++i) {
                    if (this.brewingItemStacks[i] != null && this.brewingItemStacks[i].getItem() == Items.potionitem) {
                        final int j = this.brewingItemStacks[i].getMetadata();
                        final int k = this.getPotionResult(j, itemstack);

                        if (!ItemPotion.isSplash(j) && ItemPotion.isSplash(k)) {
                            flag = true;
                            break;
                        }

                        final List<PotionEffect> list = Items.potionitem.getEffects(j);
                        final List<PotionEffect> list1 = Items.potionitem.getEffects(k);

                        if ((j <= 0 || list != list1) && (list == null || !list.equals(list1) && list1 != null) && j != k) {
                            flag = true;
                            break;
                        }
                    }
                }

                return flag;
            }
        } else {
            return false;
        }
    }

    private void brewPotions() {
        if (this.canBrew()) {
            final ItemStack itemstack = this.brewingItemStacks[3];

            for (int i = 0; i < 3; ++i) {
                if (this.brewingItemStacks[i] != null && this.brewingItemStacks[i].getItem() == Items.potionitem) {
                    final int j = this.brewingItemStacks[i].getMetadata();
                    final int k = this.getPotionResult(j, itemstack);
                    final List<PotionEffect> list = Items.potionitem.getEffects(j);
                    final List<PotionEffect> list1 = Items.potionitem.getEffects(k);

                    if (j > 0 && list == list1 || list != null && (list.equals(list1) || list1 == null)) {
                        if (!ItemPotion.isSplash(j) && ItemPotion.isSplash(k)) {
                            this.brewingItemStacks[i].setItemDamage(k);
                        }
                    } else if (j != k) {
                        this.brewingItemStacks[i].setItemDamage(k);
                    }
                }
            }

            if (itemstack.getItem().hasContainerItem()) {
                this.brewingItemStacks[3] = new ItemStack(itemstack.getItem().getContainerItem());
            } else {
                --this.brewingItemStacks[3].stackSize;

                if (this.brewingItemStacks[3].stackSize <= 0) {
                    this.brewingItemStacks[3] = null;
                }
            }
        }
    }

    /**
     * The result of brewing a potion of the specified damage value with an ingredient itemstack.
     */
    private int getPotionResult(final int meta, final ItemStack stack) {
        return stack == null ? meta : (stack.getItem().isPotionIngredient(stack) ? PotionHelper.applyIngredient(meta, stack.getItem().getPotionEffect(stack)) : meta);
    }

    public void readFromNBT(final NBTTagCompound compound) {
        super.readFromNBT(compound);
        final NBTTagList nbttaglist = compound.getTagList("Items", 10);
        this.brewingItemStacks = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            final NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            final int j = nbttagcompound.getByte("Slot");

            if (j >= 0 && j < this.brewingItemStacks.length) {
                this.brewingItemStacks[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
            }
        }

        this.brewTime = compound.getShort("BrewTime");

        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
    }

    public void writeToNBT(final NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setShort("BrewTime", (short) this.brewTime);
        final NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.brewingItemStacks.length; ++i) {
            if (this.brewingItemStacks[i] != null) {
                final NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                this.brewingItemStacks[i].writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        compound.setTag("Items", nbttaglist);

        if (this.hasCustomName()) {
            compound.setString("CustomName", this.customName);
        }
    }

    /**
     * Returns the stack in the given slot.
     *
     * @param index The slot to retrieve from.
     */
    public ItemStack getStackInSlot(final int index) {
        return index >= 0 && index < this.brewingItemStacks.length ? this.brewingItemStacks[index] : null;
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     *
     * @param index The slot to remove from.
     * @param count The maximum amount of items to remove.
     */
    public ItemStack decrStackSize(final int index, final int count) {
        if (index >= 0 && index < this.brewingItemStacks.length) {
            final ItemStack itemstack = this.brewingItemStacks[index];
            this.brewingItemStacks[index] = null;
            return itemstack;
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
        if (index >= 0 && index < this.brewingItemStacks.length) {
            final ItemStack itemstack = this.brewingItemStacks[index];
            this.brewingItemStacks[index] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(final int index, final ItemStack stack) {
        if (index >= 0 && index < this.brewingItemStacks.length) {
            this.brewingItemStacks[index] = stack;
        }
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    public int getInventoryStackLimit() {
        return 64;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(final EntityPlayer player) {
        return this.worldObj.getTileEntity(this.pos) == this && player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    public void openInventory(final EntityPlayer player) {
    }

    public void closeInventory(final EntityPlayer player) {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(final int index, final ItemStack stack) {
        return index == 3 ? stack.getItem().isPotionIngredient(stack) : stack.getItem() == Items.potionitem || stack.getItem() == Items.glass_bottle;
    }

    public boolean[] func_174902_m() {
        final boolean[] aboolean = new boolean[3];

        for (int i = 0; i < 3; ++i) {
            if (this.brewingItemStacks[i] != null) {
                aboolean[i] = true;
            }
        }

        return aboolean;
    }

    public int[] getSlotsForFace(final EnumFacing side) {
        return side == EnumFacing.UP ? inputSlots : outputSlots;
    }

    /**
     * Returns true if automation can insert the given item in the given slot from the given side. Args: slot, item,
     * side
     */
    public boolean canInsertItem(final int index, final ItemStack itemStackIn, final EnumFacing direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    /**
     * Returns true if automation can extract the given item in the given slot from the given side. Args: slot, item,
     * side
     */
    public boolean canExtractItem(final int index, final ItemStack stack, final EnumFacing direction) {
        return true;
    }

    public String getGuiID() {
        return "minecraft:brewing_stand";
    }

    public Container createContainer(final InventoryPlayer playerInventory, final EntityPlayer playerIn) {
        return new ContainerBrewingStand(playerInventory, this);
    }

    public int getField(final int id) {
        switch (id) {
            case 0:
                return this.brewTime;

            default:
                return 0;
        }
    }

    public void setField(final int id, final int value) {
        switch (id) {
            case 0:
                this.brewTime = value;

            default:
        }
    }

    public int getFieldCount() {
        return 1;
    }

    public void clear() {
        for (int i = 0; i < this.brewingItemStacks.length; ++i) {
            this.brewingItemStacks[i] = null;
        }
    }
}
