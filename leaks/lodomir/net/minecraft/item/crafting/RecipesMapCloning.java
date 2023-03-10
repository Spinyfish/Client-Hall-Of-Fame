/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipesMapCloning
implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int i = 0;
        ItemStack itemstack = null;
        for (int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack1 = inv.getStackInSlot(j);
            if (itemstack1 == null) continue;
            if (itemstack1.getItem() == Items.filled_map) {
                if (itemstack != null) {
                    return false;
                }
                itemstack = itemstack1;
                continue;
            }
            if (itemstack1.getItem() != Items.map) {
                return false;
            }
            ++i;
        }
        return itemstack != null && i > 0;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        int i = 0;
        ItemStack itemstack = null;
        for (int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack1 = inv.getStackInSlot(j);
            if (itemstack1 == null) continue;
            if (itemstack1.getItem() == Items.filled_map) {
                if (itemstack != null) {
                    return null;
                }
                itemstack = itemstack1;
                continue;
            }
            if (itemstack1.getItem() != Items.map) {
                return null;
            }
            ++i;
        }
        if (itemstack != null && i >= 1) {
            ItemStack itemstack2 = new ItemStack(Items.filled_map, i + 1, itemstack.getMetadata());
            if (itemstack.hasDisplayName()) {
                itemstack2.setStackDisplayName(itemstack.getDisplayName());
            }
            return itemstack2;
        }
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 9;
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

