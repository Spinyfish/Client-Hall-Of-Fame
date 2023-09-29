package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

public class TileEntityHopper extends TileEntityLockable implements IHopper, ITickable {
    private ItemStack[] inventory = new ItemStack[5];
    private String customName;
    private int transferCooldown = -1;

    public void readFromNBT(final NBTTagCompound compound) {
        super.readFromNBT(compound);
        final NBTTagList nbttaglist = compound.getTagList("Items", 10);
        this.inventory = new ItemStack[this.getSizeInventory()];

        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }

        this.transferCooldown = compound.getInteger("TransferCooldown");

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            final NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            final int j = nbttagcompound.getByte("Slot");

            if (j >= 0 && j < this.inventory.length) {
                this.inventory[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
            }
        }
    }

    public void writeToNBT(final NBTTagCompound compound) {
        super.writeToNBT(compound);
        final NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.inventory.length; ++i) {
            if (this.inventory[i] != null) {
                final NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        compound.setTag("Items", nbttaglist);
        compound.setInteger("TransferCooldown", this.transferCooldown);

        if (this.hasCustomName()) {
            compound.setString("CustomName", this.customName);
        }
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty() {
        super.markDirty();
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory() {
        return this.inventory.length;
    }

    /**
     * Returns the stack in the given slot.
     *
     * @param index The slot to retrieve from.
     */
    public ItemStack getStackInSlot(final int index) {
        return this.inventory[index];
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     *
     * @param index The slot to remove from.
     * @param count The maximum amount of items to remove.
     */
    public ItemStack decrStackSize(final int index, final int count) {
        if (this.inventory[index] != null) {
            if (this.inventory[index].stackSize <= count) {
                final ItemStack itemstack1 = this.inventory[index];
                this.inventory[index] = null;
                return itemstack1;
            } else {
                final ItemStack itemstack = this.inventory[index].splitStack(count);

                if (this.inventory[index].stackSize == 0) {
                    this.inventory[index] = null;
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
        if (this.inventory[index] != null) {
            final ItemStack itemstack = this.inventory[index];
            this.inventory[index] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(final int index, final ItemStack stack) {
        this.inventory[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getCommandSenderName() {
        return this.hasCustomName() ? this.customName : "container.hopper";
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName() {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setCustomName(final String customNameIn) {
        this.customName = customNameIn;
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
        return true;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update() {
        if (this.worldObj != null && !this.worldObj.isRemote) {
            --this.transferCooldown;

            if (!this.isOnTransferCooldown()) {
                this.setTransferCooldown(0);
                this.updateHopper();
            }
        }
    }

    public boolean updateHopper() {
        if (this.worldObj != null && !this.worldObj.isRemote) {
            if (!this.isOnTransferCooldown() && BlockHopper.isEnabled(this.getBlockMetadata())) {
                boolean flag = false;

                if (!this.isEmpty()) {
                    flag = this.transferItemsOut();
                }

                if (!this.isFull()) {
                    flag = captureDroppedItems(this) || flag;
                }

                if (flag) {
                    this.setTransferCooldown(8);
                    this.markDirty();
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    private boolean isEmpty() {
        for (final ItemStack itemstack : this.inventory) {
            if (itemstack != null) {
                return false;
            }
        }

        return true;
    }

    private boolean isFull() {
        for (final ItemStack itemstack : this.inventory) {
            if (itemstack == null || itemstack.stackSize != itemstack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    private boolean transferItemsOut() {
        final IInventory iinventory = this.getInventoryForHopperTransfer();

        if (iinventory == null) {
            return false;
        } else {
            final EnumFacing enumfacing = BlockHopper.getFacing(this.getBlockMetadata()).getOpposite();

            if (this.isInventoryFull(iinventory, enumfacing)) {
                return false;
            } else {
                for (int i = 0; i < this.getSizeInventory(); ++i) {
                    if (this.getStackInSlot(i) != null) {
                        final ItemStack itemstack = this.getStackInSlot(i).copy();
                        final ItemStack itemstack1 = putStackInInventoryAllSlots(iinventory, this.decrStackSize(i, 1), enumfacing);

                        if (itemstack1 == null || itemstack1.stackSize == 0) {
                            iinventory.markDirty();
                            return true;
                        }

                        this.setInventorySlotContents(i, itemstack);
                    }
                }

                return false;
            }
        }
    }

    /**
     * Returns false if the inventory has any room to place items in
     *
     * @param inventoryIn The inventory to check
     * @param side        The side to check from
     */
    private boolean isInventoryFull(final IInventory inventoryIn, final EnumFacing side) {
        if (inventoryIn instanceof ISidedInventory) {
            final ISidedInventory isidedinventory = (ISidedInventory) inventoryIn;
            final int[] aint = isidedinventory.getSlotsForFace(side);

            for (int k = 0; k < aint.length; ++k) {
                final ItemStack itemstack1 = isidedinventory.getStackInSlot(aint[k]);

                if (itemstack1 == null || itemstack1.stackSize != itemstack1.getMaxStackSize()) {
                    return false;
                }
            }
        } else {
            final int i = inventoryIn.getSizeInventory();

            for (int j = 0; j < i; ++j) {
                final ItemStack itemstack = inventoryIn.getStackInSlot(j);

                if (itemstack == null || itemstack.stackSize != itemstack.getMaxStackSize()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns false if the specified IInventory contains any items
     *
     * @param inventoryIn The inventory to check
     * @param side        The side to access the inventory from
     */
    private static boolean isInventoryEmpty(final IInventory inventoryIn, final EnumFacing side) {
        if (inventoryIn instanceof ISidedInventory) {
            final ISidedInventory isidedinventory = (ISidedInventory) inventoryIn;
            final int[] aint = isidedinventory.getSlotsForFace(side);

            for (int i = 0; i < aint.length; ++i) {
                if (isidedinventory.getStackInSlot(aint[i]) != null) {
                    return false;
                }
            }
        } else {
            final int j = inventoryIn.getSizeInventory();

            for (int k = 0; k < j; ++k) {
                if (inventoryIn.getStackInSlot(k) != null) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean captureDroppedItems(final IHopper p_145891_0_) {
        final IInventory iinventory = getHopperInventory(p_145891_0_);

        if (iinventory != null) {
            final EnumFacing enumfacing = EnumFacing.DOWN;

            if (isInventoryEmpty(iinventory, enumfacing)) {
                return false;
            }

            if (iinventory instanceof ISidedInventory) {
                final ISidedInventory isidedinventory = (ISidedInventory) iinventory;
                final int[] aint = isidedinventory.getSlotsForFace(enumfacing);

                for (int i = 0; i < aint.length; ++i) {
                    if (pullItemFromSlot(p_145891_0_, iinventory, aint[i], enumfacing)) {
                        return true;
                    }
                }
            } else {
                final int j = iinventory.getSizeInventory();

                for (int k = 0; k < j; ++k) {
                    if (pullItemFromSlot(p_145891_0_, iinventory, k, enumfacing)) {
                        return true;
                    }
                }
            }
        } else {
            for (final EntityItem entityitem : func_181556_a(p_145891_0_.getWorld(), p_145891_0_.getXPos(), p_145891_0_.getYPos() + 1.0D, p_145891_0_.getZPos())) {
                if (putDropInInventoryAllSlots(p_145891_0_, entityitem)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Pulls from the specified slot in the inventory and places in any available slot in the hopper. Returns true if
     * the entire stack was moved
     *
     * @param index The slot to pull from
     */
    private static boolean pullItemFromSlot(final IHopper hopper, final IInventory inventoryIn, final int index, final EnumFacing direction) {
        final ItemStack itemstack = inventoryIn.getStackInSlot(index);

        if (itemstack != null && canExtractItemFromSlot(inventoryIn, itemstack, index, direction)) {
            final ItemStack itemstack1 = itemstack.copy();
            final ItemStack itemstack2 = putStackInInventoryAllSlots(hopper, inventoryIn.decrStackSize(index, 1), null);

            if (itemstack2 == null || itemstack2.stackSize == 0) {
                inventoryIn.markDirty();
                return true;
            }

            inventoryIn.setInventorySlotContents(index, itemstack1);
        }

        return false;
    }

    /**
     * Attempts to place the passed EntityItem's stack into the inventory using as many slots as possible. Returns false
     * if the stackSize of the drop was not depleted.
     */
    public static boolean putDropInInventoryAllSlots(final IInventory p_145898_0_, final EntityItem itemIn) {
        boolean flag = false;

        if (itemIn == null) {
            return false;
        } else {
            final ItemStack itemstack = itemIn.getEntityItem().copy();
            final ItemStack itemstack1 = putStackInInventoryAllSlots(p_145898_0_, itemstack, null);

            if (itemstack1 != null && itemstack1.stackSize != 0) {
                itemIn.setEntityItemStack(itemstack1);
            } else {
                flag = true;
                itemIn.setDead();
            }

            return flag;
        }
    }

    /**
     * Attempts to place the passed stack in the inventory, using as many slots as required. Returns leftover items
     */
    public static ItemStack putStackInInventoryAllSlots(final IInventory inventoryIn, ItemStack stack, final EnumFacing side) {
        if (inventoryIn instanceof ISidedInventory && side != null) {
            final ISidedInventory isidedinventory = (ISidedInventory) inventoryIn;
            final int[] aint = isidedinventory.getSlotsForFace(side);

            for (int k = 0; k < aint.length && stack != null && stack.stackSize > 0; ++k) {
                stack = insertStack(inventoryIn, stack, aint[k], side);
            }
        } else {
            final int i = inventoryIn.getSizeInventory();

            for (int j = 0; j < i && stack != null && stack.stackSize > 0; ++j) {
                stack = insertStack(inventoryIn, stack, j, side);
            }
        }

        if (stack != null && stack.stackSize == 0) {
            stack = null;
        }

        return stack;
    }

    /**
     * Can this hopper insert the specified item from the specified slot on the specified side?
     *
     * @param inventoryIn The inventory to check if insertable
     * @param stack       The stack to check if insertable
     * @param index       The slot to check if insertable
     * @param side        The side to check if insertable
     */
    private static boolean canInsertItemInSlot(final IInventory inventoryIn, final ItemStack stack, final int index, final EnumFacing side) {
        return inventoryIn.isItemValidForSlot(index, stack) && (!(inventoryIn instanceof ISidedInventory) || ((ISidedInventory) inventoryIn).canInsertItem(index, stack, side));
    }

    /**
     * Can this hopper extract the specified item from the specified slot on the specified side?
     *
     * @param inventoryIn The inventory to check
     * @param stack       Item to check if extractable
     * @param index       Slot to check if extractable
     * @param side        Side to check if extractable
     */
    private static boolean canExtractItemFromSlot(final IInventory inventoryIn, final ItemStack stack, final int index, final EnumFacing side) {
        return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory) inventoryIn).canExtractItem(index, stack, side);
    }

    /**
     * Insert the specified stack to the specified inventory and return any leftover items
     *
     * @param inventoryIn The inventory to insert to
     * @param stack       The stack to try and insert
     * @param index       The slot to try and put items in
     * @param side        The side to insert from
     */
    private static ItemStack insertStack(final IInventory inventoryIn, ItemStack stack, final int index, final EnumFacing side) {
        final ItemStack itemstack = inventoryIn.getStackInSlot(index);

        if (canInsertItemInSlot(inventoryIn, stack, index, side)) {
            boolean flag = false;

            if (itemstack == null) {
                inventoryIn.setInventorySlotContents(index, stack);
                stack = null;
                flag = true;
            } else if (canCombine(itemstack, stack)) {
                final int i = stack.getMaxStackSize() - itemstack.stackSize;
                final int j = Math.min(stack.stackSize, i);
                stack.stackSize -= j;
                itemstack.stackSize += j;
                flag = j > 0;
            }

            if (flag) {
                if (inventoryIn instanceof TileEntityHopper) {
                    final TileEntityHopper tileentityhopper = (TileEntityHopper) inventoryIn;

                    if (tileentityhopper.mayTransfer()) {
                        tileentityhopper.setTransferCooldown(8);
                    }

                    inventoryIn.markDirty();
                }

                inventoryIn.markDirty();
            }
        }

        return stack;
    }

    /**
     * Returns the IInventory that this hopper is pointing into
     */
    private IInventory getInventoryForHopperTransfer() {
        final EnumFacing enumfacing = BlockHopper.getFacing(this.getBlockMetadata());
        return getInventoryAtPosition(this.getWorld(), this.pos.getX() + enumfacing.getFrontOffsetX(), this.pos.getY() + enumfacing.getFrontOffsetY(), this.pos.getZ() + enumfacing.getFrontOffsetZ());
    }

    /**
     * Returns the IInventory for the specified hopper
     *
     * @param hopper The hopper the return an inventory for
     */
    public static IInventory getHopperInventory(final IHopper hopper) {
        return getInventoryAtPosition(hopper.getWorld(), hopper.getXPos(), hopper.getYPos() + 1.0D, hopper.getZPos());
    }

    public static List<EntityItem> func_181556_a(final World p_181556_0_, final double p_181556_1_, final double p_181556_3_, final double p_181556_5_) {
        return p_181556_0_.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(p_181556_1_ - 0.5D, p_181556_3_ - 0.5D, p_181556_5_ - 0.5D, p_181556_1_ + 0.5D, p_181556_3_ + 0.5D, p_181556_5_ + 0.5D), EntitySelectors.selectAnything);
    }

    /**
     * Returns the IInventory (if applicable) of the TileEntity at the specified position
     */
    public static IInventory getInventoryAtPosition(final World worldIn, final double x, final double y, final double z) {
        IInventory iinventory = null;
        final int i = MathHelper.floor_double(x);
        final int j = MathHelper.floor_double(y);
        final int k = MathHelper.floor_double(z);
        final BlockPos blockpos = new BlockPos(i, j, k);
        final Block block = worldIn.getBlockState(blockpos).getBlock();

        if (block.hasTileEntity()) {
            final TileEntity tileentity = worldIn.getTileEntity(blockpos);

            if (tileentity instanceof IInventory) {
                iinventory = (IInventory) tileentity;

                if (iinventory instanceof TileEntityChest && block instanceof BlockChest) {
                    iinventory = ((BlockChest) block).getLockableContainer(worldIn, blockpos);
                }
            }
        }

        if (iinventory == null) {
            final List<Entity> list = worldIn.getEntitiesInAABBexcluding(null, new AxisAlignedBB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D), EntitySelectors.selectInventories);

            if (list.size() > 0) {
                iinventory = (IInventory) list.get(worldIn.rand.nextInt(list.size()));
            }
        }

        return iinventory;
    }

    private static boolean canCombine(final ItemStack stack1, final ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && (stack1.getMetadata() == stack2.getMetadata() && (stack1.stackSize <= stack1.getMaxStackSize() && ItemStack.areItemStackTagsEqual(stack1, stack2)));
    }

    /**
     * Gets the world X position for this hopper entity.
     */
    public double getXPos() {
        return (double) this.pos.getX() + 0.5D;
    }

    /**
     * Gets the world Y position for this hopper entity.
     */
    public double getYPos() {
        return (double) this.pos.getY() + 0.5D;
    }

    /**
     * Gets the world Z position for this hopper entity.
     */
    public double getZPos() {
        return (double) this.pos.getZ() + 0.5D;
    }

    public void setTransferCooldown(final int ticks) {
        this.transferCooldown = ticks;
    }

    public boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }

    public boolean mayTransfer() {
        return this.transferCooldown <= 1;
    }

    public String getGuiID() {
        return "minecraft:hopper";
    }

    public Container createContainer(final InventoryPlayer playerInventory, final EntityPlayer playerIn) {
        return new ContainerHopper(playerInventory, this, playerIn);
    }

    public int getField(final int id) {
        return 0;
    }

    public void setField(final int id, final int value) {
    }

    public int getFieldCount() {
        return 0;
    }

    public void clear() {
        for (int i = 0; i < this.inventory.length; ++i) {
            this.inventory[i] = null;
        }
    }
}
