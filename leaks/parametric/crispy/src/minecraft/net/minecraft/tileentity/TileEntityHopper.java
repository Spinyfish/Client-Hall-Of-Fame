package net.minecraft.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.command.IEntitySelector;
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
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityHopper extends TileEntityLockable implements IHopper, IUpdatePlayerListBox
{
    private ItemStack[] inventory = new ItemStack[5];
    private String customName;
    private int transferCooldown = -1;

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        NBTTagList var2 = compound.getTagList("Items", 10);
        this.inventory = new ItemStack[this.getSizeInventory()];

        if (compound.hasKey("CustomName", 8))
        {
            this.customName = compound.getString("CustomName");
        }

        this.transferCooldown = compound.getInteger("TransferCooldown");

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            byte var5 = var4.getByte("Slot");

            if (var5 >= 0 && var5 < this.inventory.length)
            {
                this.inventory[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < this.inventory.length; ++var3)
        {
            if (this.inventory[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.inventory[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        compound.setTag("Items", var2);
        compound.setInteger("TransferCooldown", this.transferCooldown);

        if (this.hasCustomName())
        {
            compound.setString("CustomName", this.customName);
        }
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty()
    {
        super.markDirty();
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.inventory.length;
    }

    /**
     * Returns the stack in the given slot.
     *  
     * @param index The slot to retrieve from.
     */
    public ItemStack getStackInSlot(int index)
    {
        return this.inventory[index];
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     *  
     * @param index The slot to remove from.
     * @param count The maximum amount of items to remove.
     */
    public ItemStack decrStackSize(int index, int count)
    {
        if (this.inventory[index] != null)
        {
            ItemStack var3;

            if (this.inventory[index].stackSize <= count)
            {
                var3 = this.inventory[index];
                this.inventory[index] = null;
                return var3;
            }
            else
            {
                var3 = this.inventory[index].splitStack(count);

                if (this.inventory[index].stackSize == 0)
                {
                    this.inventory[index] = null;
                }

                return var3;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Removes a stack from the given slot and returns it.
     *  
     * @param index The slot to remove a stack from.
     */
    public ItemStack getStackInSlotOnClosing(int index)
    {
        if (this.inventory[index] != null)
        {
            ItemStack var2 = this.inventory[index];
            this.inventory[index] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.inventory[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getCommandSenderName()
    {
        return this.hasCustomName() ? this.customName : "container.hopper";
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName()
    {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setCustomName(String customNameIn)
    {
        this.customName = customNameIn;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
    }

    public void openInventory(EntityPlayer player) {}

    public void closeInventory(EntityPlayer player) {}

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update()
    {
        if (this.worldObj != null && !this.worldObj.isRemote)
        {
            --this.transferCooldown;

            if (!this.isOnTransferCooldown())
            {
                this.setTransferCooldown(0);
                this.updateHopper();
            }
        }
    }

    public boolean updateHopper()
    {
        if (this.worldObj != null && !this.worldObj.isRemote)
        {
            if (!this.isOnTransferCooldown() && BlockHopper.isEnabled(this.getBlockMetadata()))
            {
                boolean var1 = false;

                if (!this.isEmpty())
                {
                    var1 = this.transferItemsOut();
                }

                if (!this.isFull())
                {
                    var1 = captureDroppedItems(this) || var1;
                }

                if (var1)
                {
                    this.setTransferCooldown(8);
                    this.markDirty();
                    return true;
                }
            }

            return false;
        }
        else
        {
            return false;
        }
    }

    private boolean isEmpty()
    {
        ItemStack[] var1 = this.inventory;
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3)
        {
            ItemStack var4 = var1[var3];

            if (var4 != null)
            {
                return false;
            }
        }

        return true;
    }

    private boolean isFull()
    {
        ItemStack[] var1 = this.inventory;
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3)
        {
            ItemStack var4 = var1[var3];

            if (var4 == null || var4.stackSize != var4.getMaxStackSize())
            {
                return false;
            }
        }

        return true;
    }

    private boolean transferItemsOut()
    {
        IInventory var1 = this.getInventoryForHopperTransfer();

        if (var1 == null)
        {
            return false;
        }
        else
        {
            EnumFacing var2 = BlockHopper.getFacing(this.getBlockMetadata()).getOpposite();

            if (this.isInventoryFull(var1, var2))
            {
                return false;
            }
            else
            {
                for (int var3 = 0; var3 < this.getSizeInventory(); ++var3)
                {
                    if (this.getStackInSlot(var3) != null)
                    {
                        ItemStack var4 = this.getStackInSlot(var3).copy();
                        ItemStack var5 = putStackInInventoryAllSlots(var1, this.decrStackSize(var3, 1), var2);

                        if (var5 == null || var5.stackSize == 0)
                        {
                            var1.markDirty();
                            return true;
                        }

                        this.setInventorySlotContents(var3, var4);
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
     * @param side The side to check from
     */
    private boolean isInventoryFull(IInventory inventoryIn, EnumFacing side)
    {
        if (inventoryIn instanceof ISidedInventory)
        {
            ISidedInventory var3 = (ISidedInventory)inventoryIn;
            int[] var4 = var3.getSlotsForFace(side);

            for (int var5 = 0; var5 < var4.length; ++var5)
            {
                ItemStack var6 = var3.getStackInSlot(var4[var5]);

                if (var6 == null || var6.stackSize != var6.getMaxStackSize())
                {
                    return false;
                }
            }
        }
        else
        {
            int var7 = inventoryIn.getSizeInventory();

            for (int var8 = 0; var8 < var7; ++var8)
            {
                ItemStack var9 = inventoryIn.getStackInSlot(var8);

                if (var9 == null || var9.stackSize != var9.getMaxStackSize())
                {
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
     * @param side The side to access the inventory from
     */
    private static boolean isInventoryEmpty(IInventory inventoryIn, EnumFacing side)
    {
        if (inventoryIn instanceof ISidedInventory)
        {
            ISidedInventory var2 = (ISidedInventory)inventoryIn;
            int[] var3 = var2.getSlotsForFace(side);

            for (int var4 = 0; var4 < var3.length; ++var4)
            {
                if (var2.getStackInSlot(var3[var4]) != null)
                {
                    return false;
                }
            }
        }
        else
        {
            int var5 = inventoryIn.getSizeInventory();

            for (int var6 = 0; var6 < var5; ++var6)
            {
                if (inventoryIn.getStackInSlot(var6) != null)
                {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean captureDroppedItems(IHopper p_145891_0_)
    {
        IInventory var1 = getHopperInventory(p_145891_0_);

        if (var1 != null)
        {
            EnumFacing var2 = EnumFacing.DOWN;

            if (isInventoryEmpty(var1, var2))
            {
                return false;
            }

            if (var1 instanceof ISidedInventory)
            {
                ISidedInventory var3 = (ISidedInventory)var1;
                int[] var4 = var3.getSlotsForFace(var2);

                for (int var5 = 0; var5 < var4.length; ++var5)
                {
                    if (pullItemFromSlot(p_145891_0_, var1, var4[var5], var2))
                    {
                        return true;
                    }
                }
            }
            else
            {
                int var7 = var1.getSizeInventory();

                for (int var8 = 0; var8 < var7; ++var8)
                {
                    if (pullItemFromSlot(p_145891_0_, var1, var8, var2))
                    {
                        return true;
                    }
                }
            }
        }
        else
        {
            EntityItem var6 = getItemInBlock(p_145891_0_.getWorld(), p_145891_0_.getXPos(), p_145891_0_.getYPos() + 1.0D, p_145891_0_.getZPos());

            if (var6 != null)
            {
                return putDropInInventoryAllSlots(p_145891_0_, var6);
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
    private static boolean pullItemFromSlot(IHopper hopper, IInventory inventoryIn, int index, EnumFacing direction)
    {
        ItemStack var4 = inventoryIn.getStackInSlot(index);

        if (var4 != null && canExtractItemFromSlot(inventoryIn, var4, index, direction))
        {
            ItemStack var5 = var4.copy();
            ItemStack var6 = putStackInInventoryAllSlots(hopper, inventoryIn.decrStackSize(index, 1), (EnumFacing)null);

            if (var6 == null || var6.stackSize == 0)
            {
                inventoryIn.markDirty();
                return true;
            }

            inventoryIn.setInventorySlotContents(index, var5);
        }

        return false;
    }

    /**
     * Attempts to place the passed EntityItem's stack into the inventory using as many slots as possible. Returns false
     * if the stackSize of the drop was not depleted.
     */
    public static boolean putDropInInventoryAllSlots(IInventory p_145898_0_, EntityItem itemIn)
    {
        boolean var2 = false;

        if (itemIn == null)
        {
            return false;
        }
        else
        {
            ItemStack var3 = itemIn.getEntityItem().copy();
            ItemStack var4 = putStackInInventoryAllSlots(p_145898_0_, var3, (EnumFacing)null);

            if (var4 != null && var4.stackSize != 0)
            {
                itemIn.setEntityItemStack(var4);
            }
            else
            {
                var2 = true;
                itemIn.setDead();
            }

            return var2;
        }
    }

    /**
     * Attempts to place the passed stack in the inventory, using as many slots as required. Returns leftover items
     */
    public static ItemStack putStackInInventoryAllSlots(IInventory inventoryIn, ItemStack stack, EnumFacing side)
    {
        if (inventoryIn instanceof ISidedInventory && side != null)
        {
            ISidedInventory var6 = (ISidedInventory)inventoryIn;
            int[] var7 = var6.getSlotsForFace(side);

            for (int var5 = 0; var5 < var7.length && stack != null && stack.stackSize > 0; ++var5)
            {
                stack = insertStack(inventoryIn, stack, var7[var5], side);
            }
        }
        else
        {
            int var3 = inventoryIn.getSizeInventory();

            for (int var4 = 0; var4 < var3 && stack != null && stack.stackSize > 0; ++var4)
            {
                stack = insertStack(inventoryIn, stack, var4, side);
            }
        }

        if (stack != null && stack.stackSize == 0)
        {
            stack = null;
        }

        return stack;
    }

    /**
     * Can this hopper insert the specified item from the specified slot on the specified side?
     *  
     * @param inventoryIn The inventory to check if insertable
     * @param stack The stack to check if insertable
     * @param index The slot to check if insertable
     * @param side The side to check if insertable
     */
    private static boolean canInsertItemInSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side)
    {
        return !inventoryIn.isItemValidForSlot(index, stack) ? false : !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canInsertItem(index, stack, side);
    }

    /**
     * Can this hopper extract the specified item from the specified slot on the specified side?
     *  
     * @param inventoryIn The inventory to check
     * @param stack Item to check if extractable
     * @param index Slot to check if extractable
     * @param side Side to check if extractable
     */
    private static boolean canExtractItemFromSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side)
    {
        return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canExtractItem(index, stack, side);
    }

    /**
     * Insert the specified stack to the specified inventory and return any leftover items
     *  
     * @param inventoryIn The inventory to insert to
     * @param stack The stack to try and insert
     * @param index The slot to try and put items in
     * @param side The side to insert from
     */
    private static ItemStack insertStack(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side)
    {
        ItemStack var4 = inventoryIn.getStackInSlot(index);

        if (canInsertItemInSlot(inventoryIn, stack, index, side))
        {
            boolean var5 = false;

            if (var4 == null)
            {
                inventoryIn.setInventorySlotContents(index, stack);
                stack = null;
                var5 = true;
            }
            else if (canCombine(var4, stack))
            {
                int var6 = stack.getMaxStackSize() - var4.stackSize;
                int var7 = Math.min(stack.stackSize, var6);
                stack.stackSize -= var7;
                var4.stackSize += var7;
                var5 = var7 > 0;
            }

            if (var5)
            {
                if (inventoryIn instanceof TileEntityHopper)
                {
                    TileEntityHopper var8 = (TileEntityHopper)inventoryIn;

                    if (var8.mayTransfer())
                    {
                        var8.setTransferCooldown(8);
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
    private IInventory getInventoryForHopperTransfer()
    {
        EnumFacing var1 = BlockHopper.getFacing(this.getBlockMetadata());
        return getInventoryAtPosition(this.getWorld(), (double)(this.pos.getX() + var1.getFrontOffsetX()), (double)(this.pos.getY() + var1.getFrontOffsetY()), (double)(this.pos.getZ() + var1.getFrontOffsetZ()));
    }

    /**
     * Returns the IInventory for the specified hopper
     *  
     * @param hopper The hopper the return an inventory for
     */
    public static IInventory getHopperInventory(IHopper hopper)
    {
        return getInventoryAtPosition(hopper.getWorld(), hopper.getXPos(), hopper.getYPos() + 1.0D, hopper.getZPos());
    }

    /**
     * Returns the first dropped item in the block space specified
     */
    public static EntityItem getItemInBlock(World worldIn, double x, double y, double z)
    {
        List var7 = worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D), IEntitySelector.selectAnything);
        return var7.size() > 0 ? (EntityItem)var7.get(0) : null;
    }

    /**
     * Returns the IInventory (if applicable) of the TileEntity at the specified position
     */
    public static IInventory getInventoryAtPosition(World worldIn, double x, double y, double z)
    {
        Object var7 = null;
        int var8 = MathHelper.floor_double(x);
        int var9 = MathHelper.floor_double(y);
        int var10 = MathHelper.floor_double(z);
        BlockPos var11 = new BlockPos(var8, var9, var10);
        TileEntity var12 = worldIn.getTileEntity(new BlockPos(var8, var9, var10));

        if (var12 instanceof IInventory)
        {
            var7 = (IInventory)var12;

            if (var7 instanceof TileEntityChest)
            {
                Block var13 = worldIn.getBlockState(new BlockPos(var8, var9, var10)).getBlock();

                if (var13 instanceof BlockChest)
                {
                    var7 = ((BlockChest)var13).getLockableContainer(worldIn, var11);
                }
            }
        }

        if (var7 == null)
        {
            List var14 = worldIn.getEntitiesInAABBexcluding((Entity)null, new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D), IEntitySelector.selectInventories);

            if (var14.size() > 0)
            {
                var7 = (IInventory)var14.get(worldIn.rand.nextInt(var14.size()));
            }
        }

        return (IInventory)var7;
    }

    private static boolean canCombine(ItemStack stack1, ItemStack stack2)
    {
        return stack1.getItem() != stack2.getItem() ? false : (stack1.getMetadata() != stack2.getMetadata() ? false : (stack1.stackSize > stack1.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(stack1, stack2)));
    }

    /**
     * Gets the world X position for this hopper entity.
     */
    public double getXPos()
    {
        return (double)this.pos.getX();
    }

    /**
     * Gets the world Y position for this hopper entity.
     */
    public double getYPos()
    {
        return (double)this.pos.getY();
    }

    /**
     * Gets the world Z position for this hopper entity.
     */
    public double getZPos()
    {
        return (double)this.pos.getZ();
    }

    public void setTransferCooldown(int ticks)
    {
        this.transferCooldown = ticks;
    }

    public boolean isOnTransferCooldown()
    {
        return this.transferCooldown > 0;
    }

    public boolean mayTransfer()
    {
        return this.transferCooldown <= 1;
    }

    public String getGuiID()
    {
        return "minecraft:hopper";
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new ContainerHopper(playerInventory, this, playerIn);
    }

    public int getField(int id)
    {
        return 0;
    }

    public void setField(int id, int value) {}

    public int getFieldCount()
    {
        return 0;
    }

    public void clear()
    {
        for (int var1 = 0; var1 < this.inventory.length; ++var1)
        {
            this.inventory[var1] = null;
        }
    }
}
