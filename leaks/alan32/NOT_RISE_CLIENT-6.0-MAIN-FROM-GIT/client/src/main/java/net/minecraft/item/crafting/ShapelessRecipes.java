package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ShapelessRecipes implements IRecipe {
    /**
     * Is the ItemStack that you get when craft the recipe.
     */
    private final ItemStack recipeOutput;
    private final List<ItemStack> recipeItems;

    public ShapelessRecipes(final ItemStack output, final List<ItemStack> inputList) {
        this.recipeOutput = output;
        this.recipeItems = inputList;
    }

    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }

    public ItemStack[] getRemainingItems(final InventoryCrafting inv) {
        final ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

        for (int i = 0; i < aitemstack.length; ++i) {
            final ItemStack itemstack = inv.getStackInSlot(i);

            if (itemstack != null && itemstack.getItem().hasContainerItem()) {
                aitemstack[i] = new ItemStack(itemstack.getItem().getContainerItem());
            }
        }

        return aitemstack;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(final InventoryCrafting inv, final World worldIn) {
        final List<ItemStack> list = Lists.newArrayList(this.recipeItems);

        for (int i = 0; i < inv.getHeight(); ++i) {
            for (int j = 0; j < inv.getWidth(); ++j) {
                final ItemStack itemstack = inv.getStackInRowAndColumn(j, i);

                if (itemstack != null) {
                    boolean flag = false;

                    for (final ItemStack itemstack1 : list) {
                        if (itemstack.getItem() == itemstack1.getItem() && (itemstack1.getMetadata() == 32767 || itemstack.getMetadata() == itemstack1.getMetadata())) {
                            flag = true;
                            list.remove(itemstack1);
                            break;
                        }
                    }

                    if (!flag) {
                        return false;
                    }
                }
            }
        }

        return list.isEmpty();
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(final InventoryCrafting inv) {
        return this.recipeOutput.copy();
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize() {
        return this.recipeItems.size();
    }
}
