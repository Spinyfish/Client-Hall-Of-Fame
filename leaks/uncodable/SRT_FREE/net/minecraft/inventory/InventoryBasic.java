package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryBasic implements IInventory {
   private String inventoryTitle;
   private final int slotsCount;
   private final ItemStack[] inventoryContents;
   private List<IInvBasic> field_70480_d;
   private boolean hasCustomName;

   public InventoryBasic(String title, boolean customName, int slotCount) {
      this.inventoryTitle = title;
      this.hasCustomName = customName;
      this.slotsCount = slotCount;
      this.inventoryContents = new ItemStack[slotCount];
   }

   public InventoryBasic(IChatComponent title, int slotCount) {
      this(title.getUnformattedText(), true, slotCount);
   }

   public void func_110134_a(IInvBasic p_110134_1_) {
      if (this.field_70480_d == null) {
         this.field_70480_d = Lists.newArrayList();
      }

      this.field_70480_d.add(p_110134_1_);
   }

   public void func_110132_b(IInvBasic p_110132_1_) {
      this.field_70480_d.remove(p_110132_1_);
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return index >= 0 && index < this.inventoryContents.length ? this.inventoryContents[index] : null;
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (this.inventoryContents[index] != null) {
         if (this.inventoryContents[index].stackSize <= count) {
            ItemStack itemstack1 = this.inventoryContents[index];
            this.inventoryContents[index] = null;
            this.markDirty();
            return itemstack1;
         } else {
            ItemStack itemstack = this.inventoryContents[index].splitStack(count);
            if (this.inventoryContents[index].stackSize == 0) {
               this.inventoryContents[index] = null;
            }

            this.markDirty();
            return itemstack;
         }
      } else {
         return null;
      }
   }

   public ItemStack func_174894_a(ItemStack stack) {
      ItemStack itemstack = stack.copy();

      for(int i = 0; i < this.slotsCount; ++i) {
         ItemStack itemstack1 = this.getStackInSlot(i);
         if (itemstack1 == null) {
            this.setInventorySlotContents(i, itemstack);
            this.markDirty();
            return null;
         }

         if (ItemStack.areItemsEqual(itemstack1, itemstack)) {
            int j = Math.min(this.getInventoryStackLimit(), itemstack1.getMaxStackSize());
            int k = Math.min(itemstack.stackSize, j - itemstack1.stackSize);
            if (k > 0) {
               itemstack1.stackSize += k;
               itemstack.stackSize -= k;
               if (itemstack.stackSize <= 0) {
                  this.markDirty();
                  return null;
               }
            }
         }
      }

      if (itemstack.stackSize != stack.stackSize) {
         this.markDirty();
      }

      return itemstack;
   }

   @Override
   public ItemStack getStackInSlotOnClosing(int index) {
      if (this.inventoryContents[index] != null) {
         ItemStack itemstack = this.inventoryContents[index];
         this.inventoryContents[index] = null;
         return itemstack;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.inventoryContents[index] = stack;
      if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
         stack.stackSize = this.getInventoryStackLimit();
      }

      this.markDirty();
   }

   @Override
   public int getSizeInventory() {
      return this.slotsCount;
   }

   @Override
   public String getCommandSenderName() {
      return this.inventoryTitle;
   }

   @Override
   public boolean hasCustomName() {
      return this.hasCustomName;
   }

   public void setCustomName(String inventoryTitleIn) {
      this.hasCustomName = true;
      this.inventoryTitle = inventoryTitleIn;
   }

   @Override
   public IChatComponent getDisplayName() {
      return (IChatComponent)(this.hasCustomName()
         ? new ChatComponentText(this.getCommandSenderName())
         : new ChatComponentTranslation(this.getCommandSenderName()));
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   @Override
   public void markDirty() {
      if (this.field_70480_d != null) {
         for(IInvBasic iInvBasic : this.field_70480_d) {
            iInvBasic.onInventoryChanged(this);
         }
      }
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
      Arrays.fill(this.inventoryContents, null);
   }
}
