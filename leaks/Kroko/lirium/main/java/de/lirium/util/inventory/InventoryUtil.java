package de.lirium.util.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryUtil {

    public static int getItemSize(Item item, IInventory inventory) {
        int size = 0;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (!inventory.getStackInSlot(i).getItem().isAir()) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (stack.getItem().equals(item) && stack.stackSize != stack.getMaxStackSize())
                    size++;
            }
        }
        return size;
    }


}
