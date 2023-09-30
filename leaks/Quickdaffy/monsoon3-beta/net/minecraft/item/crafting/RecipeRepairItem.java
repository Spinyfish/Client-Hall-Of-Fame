/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeRepairItem
implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (itemstack == null) continue;
            list.add(itemstack);
            if (list.size() <= 1) continue;
            ItemStack itemstack1 = (ItemStack)list.get(0);
            if (itemstack.getItem() == itemstack1.getItem() && itemstack1.stackSize == 1 && itemstack.stackSize == 1 && itemstack1.getItem().isDamageable()) continue;
            return false;
        }
        return list.size() == 2;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (itemstack == null) continue;
            list.add(itemstack);
            if (list.size() <= 1) continue;
            ItemStack itemstack1 = (ItemStack)list.get(0);
            if (itemstack.getItem() == itemstack1.getItem() && itemstack1.stackSize == 1 && itemstack.stackSize == 1 && itemstack1.getItem().isDamageable()) continue;
            return null;
        }
        if (list.size() == 2) {
            ItemStack itemstack2 = (ItemStack)list.get(0);
            ItemStack itemstack3 = (ItemStack)list.get(1);
            if (itemstack2.getItem() == itemstack3.getItem() && itemstack2.stackSize == 1 && itemstack3.stackSize == 1 && itemstack2.getItem().isDamageable()) {
                Item item = itemstack2.getItem();
                int j = item.getMaxDamage() - itemstack2.getItemDamage();
                int k = item.getMaxDamage() - itemstack3.getItemDamage();
                int l = j + k + item.getMaxDamage() * 5 / 100;
                int i1 = item.getMaxDamage() - l;
                if (i1 < 0) {
                    i1 = 0;
                }
                return new ItemStack(itemstack2.getItem(), 1, i1);
            }
        }
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 4;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];
        for (int i = 0; i < aitemstack.length; ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (itemstack == null || !itemstack.getItem().hasContainerItem()) continue;
            aitemstack[i] = new ItemStack(itemstack.getItem().getContainerItem());
        }
        return aitemstack;
    }
}

