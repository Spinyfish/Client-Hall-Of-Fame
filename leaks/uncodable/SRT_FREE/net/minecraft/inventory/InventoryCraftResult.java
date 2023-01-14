package net.minecraft.inventory;

import java.util.Arrays;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryCraftResult implements IInventory {
   private final ItemStack[] stackResult = new ItemStack[1];

   @Override
   public int getSizeInventory() {
      return 1;
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return this.stackResult[0];
   }

   @Override
   public String getCommandSenderName() {
      return "Result";
   }

   @Override
   public boolean hasCustomName() {
      return false;
   }

   @Override
   public IChatComponent getDisplayName() {
      return (IChatComponent)(this.hasCustomName()
         ? new ChatComponentText(this.getCommandSenderName())
         : new ChatComponentTranslation(this.getCommandSenderName()));
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (this.stackResult[0] != null) {
         ItemStack itemstack = this.stackResult[0];
         this.stackResult[0] = null;
         return itemstack;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack getStackInSlotOnClosing(int index) {
      if (this.stackResult[0] != null) {
         ItemStack itemstack = this.stackResult[0];
         this.stackResult[0] = null;
         return itemstack;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.stackResult[0] = stack;
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   @Override
   public void markDirty() {
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return true;
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
      Arrays.fill(this.stackResult, null);
   }
}
