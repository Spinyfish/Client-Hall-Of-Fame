package net.minecraft.entity;

import net.minecraft.entity.player.*;
import net.minecraft.village.*;
import net.minecraft.item.*;
import net.minecraft.util.*;

public interface IMerchant
{
    void setCustomer(final EntityPlayer p0);
    
    EntityPlayer getCustomer();
    
    MerchantRecipeList getRecipes(final EntityPlayer p0);
    
    void setRecipes(final MerchantRecipeList p0);
    
    void useRecipe(final MerchantRecipe p0);
    
    void verifySellingItem(final ItemStack p0);
    
    IChatComponent getDisplayName();
}
