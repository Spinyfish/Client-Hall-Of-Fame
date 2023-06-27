package net.minecraft.inventory;

import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraft.util.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.*;
import net.minecraft.init.*;
import net.minecraft.stats.*;

public class SlotFurnaceOutput extends Slot
{
    private EntityPlayer thePlayer;
    private int field_75228_b;
    
    public SlotFurnaceOutput(final EntityPlayer player, final IInventory inventoryIn, final int slotIndex, final int xPosition, final int yPosition) {
        super(inventoryIn, slotIndex, xPosition, yPosition);
        this.thePlayer = player;
    }
    
    @Override
    public boolean isItemValid(final ItemStack stack) {
        return false;
    }
    
    @Override
    public ItemStack decrStackSize(final int amount) {
        if (this.getHasStack()) {
            this.field_75228_b += Math.min(amount, this.getStack().stackSize);
        }
        return super.decrStackSize(amount);
    }
    
    @Override
    public void onPickupFromSlot(final EntityPlayer playerIn, final ItemStack stack) {
        this.onCrafting(stack);
        super.onPickupFromSlot(playerIn, stack);
    }
    
    @Override
    protected void onCrafting(final ItemStack stack, final int amount) {
        this.field_75228_b += amount;
        this.onCrafting(stack);
    }
    
    @Override
    protected void onCrafting(final ItemStack stack) {
        stack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.field_75228_b);
        if (!this.thePlayer.worldObj.isRemote) {
            int i = this.field_75228_b;
            final float f = FurnaceRecipes.instance().getSmeltingExperience(stack);
            if (f == 0.0f) {
                i = 0;
            }
            else if (f < 1.0f) {
                int j = MathHelper.floor_float(i * f);
                if (j < MathHelper.ceiling_float_int(i * f) && Math.random() < i * f - j) {
                    ++j;
                }
                i = j;
            }
            while (i > 0) {
                final int k = EntityXPOrb.getXPSplit(i);
                i -= k;
                this.thePlayer.worldObj.spawnEntityInWorld(new EntityXPOrb(this.thePlayer.worldObj, this.thePlayer.posX, this.thePlayer.posY + 0.5, this.thePlayer.posZ + 0.5, k));
            }
        }
        this.field_75228_b = 0;
        if (stack.getItem() == Items.iron_ingot) {
            this.thePlayer.triggerAchievement(AchievementList.acquireIron);
        }
        if (stack.getItem() == Items.cooked_fish) {
            this.thePlayer.triggerAchievement(AchievementList.cookFish);
        }
    }
}
