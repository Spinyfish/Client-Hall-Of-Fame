package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityDispenser extends TileEntityLockable implements IInventory
{
    private static final Random RNG = new Random();
    private ItemStack[] stacks = new ItemStack[9];
    protected String customName;

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return 9;
    }

    /**
     * Returns the stack in the given slot.
     *  
     * @param index The slot to retrieve from.
     */
    public ItemStack getStackInSlot(int index)
    {
        return this.stacks[index];
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     *  
     * @param index The slot to remove from.
     * @param count The maximum amount of items to remove.
     */
    public ItemStack decrStackSize(int index, int count)
    {
        if (this.stacks[index] != null)
        {
            ItemStack var3;

            if (this.stacks[index].stackSize <= count)
            {
                var3 = this.stacks[index];
                this.stacks[index] = null;
                this.markDirty();
                return var3;
            }
            else
            {
                var3 = this.stacks[index].splitStack(count);

                if (this.stacks[index].stackSize == 0)
                {
                    this.stacks[index] = null;
                }

                this.markDirty();
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
        if (this.stacks[index] != null)
        {
            ItemStack var2 = this.stacks[index];
            this.stacks[index] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    public int getDispenseSlot()
    {
        int var1 = -1;
        int var2 = 1;

        for (int var3 = 0; var3 < this.stacks.length; ++var3)
        {
            if (this.stacks[var3] != null && RNG.nextInt(var2++) == 0)
            {
                var1 = var3;
            }
        }

        return var1;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.stacks[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
    }

    /**
     * Add the given ItemStack to this Dispenser. Return the Slot the Item was placed in or -1 if no free slot is
     * available.
     */
    public int addItemStack(ItemStack stack)
    {
        for (int var2 = 0; var2 < this.stacks.length; ++var2)
        {
            if (this.stacks[var2] == null || this.stacks[var2].getItem() == null)
            {
                this.setInventorySlotContents(var2, stack);
                return var2;
            }
        }

        return -1;
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getCommandSenderName()
    {
        return this.hasCustomName() ? this.customName : "container.dispenser";
    }

    public void setCustomName(String customName)
    {
        this.customName = customName;
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName()
    {
        return this.customName != null;
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        NBTTagList var2 = compound.getTagList("Items", 10);
        this.stacks = new ItemStack[this.getSizeInventory()];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            int var5 = var4.getByte("Slot") & 255;

            if (var5 >= 0 && var5 < this.stacks.length)
            {
                this.stacks[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }

        if (compound.hasKey("CustomName", 8))
        {
            this.customName = compound.getString("CustomName");
        }
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < this.stacks.length; ++var3)
        {
            if (this.stacks[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.stacks[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        compound.setTag("Items", var2);

        if (this.hasCustomName())
        {
            compound.setString("CustomName", this.customName);
        }
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

    public String getGuiID()
    {
        return "minecraft:dispenser";
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new ContainerDispenser(playerInventory, this);
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
        for (int var1 = 0; var1 < this.stacks.length; ++var1)
        {
            this.stacks[var1] = null;
        }
    }
}
