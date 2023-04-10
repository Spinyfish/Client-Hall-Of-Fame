package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryBasic implements IInventory
{
    private String inventoryTitle;
    private int slotsCount;
    private ItemStack[] inventoryContents;
    private List field_70480_d;
    private boolean hasCustomName;

    public InventoryBasic(String title, boolean customName, int slotCount)
    {
        this.inventoryTitle = title;
        this.hasCustomName = customName;
        this.slotsCount = slotCount;
        this.inventoryContents = new ItemStack[slotCount];
    }

    public InventoryBasic(IChatComponent title, int slotCount)
    {
        this(title.getUnformattedText(), true, slotCount);
    }

    public void func_110134_a(IInvBasic p_110134_1_)
    {
        if (this.field_70480_d == null)
        {
            this.field_70480_d = Lists.newArrayList();
        }

        this.field_70480_d.add(p_110134_1_);
    }

    public void func_110132_b(IInvBasic p_110132_1_)
    {
        this.field_70480_d.remove(p_110132_1_);
    }

    /**
     * Returns the stack in the given slot.
     *  
     * @param index The slot to retrieve from.
     */
    public ItemStack getStackInSlot(int index)
    {
        return index >= 0 && index < this.inventoryContents.length ? this.inventoryContents[index] : null;
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     *  
     * @param index The slot to remove from.
     * @param count The maximum amount of items to remove.
     */
    public ItemStack decrStackSize(int index, int count)
    {
        if (this.inventoryContents[index] != null)
        {
            ItemStack var3;

            if (this.inventoryContents[index].stackSize <= count)
            {
                var3 = this.inventoryContents[index];
                this.inventoryContents[index] = null;
                this.markDirty();
                return var3;
            }
            else
            {
                var3 = this.inventoryContents[index].splitStack(count);

                if (this.inventoryContents[index].stackSize == 0)
                {
                    this.inventoryContents[index] = null;
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

    public ItemStack func_174894_a(ItemStack stack)
    {
        ItemStack var2 = stack.copy();

        for (int var3 = 0; var3 < this.slotsCount; ++var3)
        {
            ItemStack var4 = this.getStackInSlot(var3);

            if (var4 == null)
            {
                this.setInventorySlotContents(var3, var2);
                this.markDirty();
                return null;
            }

            if (ItemStack.areItemsEqual(var4, var2))
            {
                int var5 = Math.min(this.getInventoryStackLimit(), var4.getMaxStackSize());
                int var6 = Math.min(var2.stackSize, var5 - var4.stackSize);

                if (var6 > 0)
                {
                    var4.stackSize += var6;
                    var2.stackSize -= var6;

                    if (var2.stackSize <= 0)
                    {
                        this.markDirty();
                        return null;
                    }
                }
            }
        }

        if (var2.stackSize != stack.stackSize)
        {
            this.markDirty();
        }

        return var2;
    }

    /**
     * Removes a stack from the given slot and returns it.
     *  
     * @param index The slot to remove a stack from.
     */
    public ItemStack getStackInSlotOnClosing(int index)
    {
        if (this.inventoryContents[index] != null)
        {
            ItemStack var2 = this.inventoryContents[index];
            this.inventoryContents[index] = null;
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
        this.inventoryContents[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.slotsCount;
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getCommandSenderName()
    {
        return this.inventoryTitle;
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName()
    {
        return this.hasCustomName;
    }

    /**
     * Sets the name of this inventory. This is displayed to the client on opening.
     */
    public void setCustomName(String inventoryTitleIn)
    {
        this.hasCustomName = true;
        this.inventoryTitle = inventoryTitleIn;
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    public IChatComponent getDisplayName()
    {
        return (IChatComponent)(this.hasCustomName() ? new ChatComponentText(this.getCommandSenderName()) : new ChatComponentTranslation(this.getCommandSenderName(), new Object[0]));
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty()
    {
        if (this.field_70480_d != null)
        {
            for (int var1 = 0; var1 < this.field_70480_d.size(); ++var1)
            {
                ((IInvBasic)this.field_70480_d.get(var1)).onInventoryChanged(this);
            }
        }
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
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
        for (int var1 = 0; var1 < this.inventoryContents.length; ++var1)
        {
            this.inventoryContents[var1] = null;
        }
    }
}
