package net.minecraft.item.crafting;

import net.minecraft.inventory.*;
import net.minecraft.world.*;
import net.minecraft.item.*;

public interface IRecipe
{
    boolean matches(final InventoryCrafting p0, final World p1);
    
    ItemStack getCraftingResult(final InventoryCrafting p0);
    
    int getRecipeSize();
    
    ItemStack getRecipeOutput();
    
    ItemStack[] getRemainingItems(final InventoryCrafting p0);
}
