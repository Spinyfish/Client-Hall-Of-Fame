/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class InventoryMerchant
implements IInventory {
    private final IMerchant theMerchant;
    private ItemStack[] theInventory = new ItemStack[3];
    private final EntityPlayer thePlayer;
    private MerchantRecipe currentRecipe;
    private int currentRecipeIndex;

    public InventoryMerchant(EntityPlayer thePlayerIn, IMerchant theMerchantIn) {
        this.thePlayer = thePlayerIn;
        this.theMerchant = theMerchantIn;
    }

    @Override
    public int getSizeInventory() {
        return this.theInventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.theInventory[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.theInventory[index] != null) {
            if (index == 2) {
                ItemStack itemstack2 = this.theInventory[index];
                this.theInventory[index] = null;
                return itemstack2;
            }
            if (this.theInventory[index].stackSize <= count) {
                ItemStack itemstack1 = this.theInventory[index];
                this.theInventory[index] = null;
                if (this.inventoryResetNeededOnSlotChange(index)) {
                    this.resetRecipeAndSlots();
                }
                return itemstack1;
            }
            ItemStack itemstack = this.theInventory[index].splitStack(count);
            if (this.theInventory[index].stackSize == 0) {
                this.theInventory[index] = null;
            }
            if (this.inventoryResetNeededOnSlotChange(index)) {
                this.resetRecipeAndSlots();
            }
            return itemstack;
        }
        return null;
    }

    private boolean inventoryResetNeededOnSlotChange(int p_70469_1_) {
        return p_70469_1_ == 0 || p_70469_1_ == 1;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (this.theInventory[index] != null) {
            ItemStack itemstack = this.theInventory[index];
            this.theInventory[index] = null;
            return itemstack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.theInventory[index] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
        if (this.inventoryResetNeededOnSlotChange(index)) {
            this.resetRecipeAndSlots();
        }
    }

    @Override
    public String getCommandSenderName() {
        return "mob.villager";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public IChatComponent getDisplayName() {
        return this.hasCustomName() ? new ChatComponentText(this.getCommandSenderName()) : new ChatComponentTranslation(this.getCommandSenderName(), new Object[0]);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.theMerchant.getCustomer() == player;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public void markDirty() {
        this.resetRecipeAndSlots();
    }

    public void resetRecipeAndSlots() {
        this.currentRecipe = null;
        ItemStack itemstack = this.theInventory[0];
        ItemStack itemstack1 = this.theInventory[1];
        if (itemstack == null) {
            itemstack = itemstack1;
            itemstack1 = null;
        }
        if (itemstack == null) {
            this.setInventorySlotContents(2, null);
        } else {
            MerchantRecipeList merchantrecipelist = this.theMerchant.getRecipes(this.thePlayer);
            if (merchantrecipelist != null) {
                MerchantRecipe merchantrecipe = merchantrecipelist.canRecipeBeUsed(itemstack, itemstack1, this.currentRecipeIndex);
                if (merchantrecipe != null && !merchantrecipe.isRecipeDisabled()) {
                    this.currentRecipe = merchantrecipe;
                    this.setInventorySlotContents(2, merchantrecipe.getItemToSell().copy());
                } else if (itemstack1 != null) {
                    merchantrecipe = merchantrecipelist.canRecipeBeUsed(itemstack1, itemstack, this.currentRecipeIndex);
                    if (merchantrecipe != null && !merchantrecipe.isRecipeDisabled()) {
                        this.currentRecipe = merchantrecipe;
                        this.setInventorySlotContents(2, merchantrecipe.getItemToSell().copy());
                    } else {
                        this.setInventorySlotContents(2, null);
                    }
                } else {
                    this.setInventorySlotContents(2, null);
                }
            }
        }
        this.theMerchant.verifySellingItem(this.getStackInSlot(2));
    }

    public MerchantRecipe getCurrentRecipe() {
        return this.currentRecipe;
    }

    public void setCurrentRecipeIndex(int currentRecipeIndexIn) {
        this.currentRecipeIndex = currentRecipeIndexIn;
        this.resetRecipeAndSlots();
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.theInventory.length; ++i) {
            this.theInventory[i] = null;
        }
    }
}

