package net.minecraft.inventory;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.MathHelper;

public class SlotFurnaceOutput extends Slot {
    /**
     * The player that is using the GUI where this slot resides.
     */
    private final EntityPlayer thePlayer;
    private int field_75228_b;

    public SlotFurnaceOutput(final EntityPlayer player, final IInventory inventoryIn, final int slotIndex, final int xPosition, final int yPosition) {
        super(inventoryIn, slotIndex, xPosition, yPosition);
        this.thePlayer = player;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(final ItemStack stack) {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize(final int amount) {
        if (this.getHasStack()) {
            this.field_75228_b += Math.min(amount, this.getStack().stackSize);
        }

        return super.decrStackSize(amount);
    }

    public void onPickupFromSlot(final EntityPlayer playerIn, final ItemStack stack) {
        this.onCrafting(stack);
        super.onPickupFromSlot(playerIn, stack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    protected void onCrafting(final ItemStack stack, final int amount) {
        this.field_75228_b += amount;
        this.onCrafting(stack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void onCrafting(final ItemStack stack) {
        stack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.field_75228_b);

        if (!this.thePlayer.worldObj.isRemote) {
            int i = this.field_75228_b;
            final float f = FurnaceRecipes.instance().getSmeltingExperience(stack);

            if (f == 0.0F) {
                i = 0;
            } else if (f < 1.0F) {
                int j = MathHelper.floor_float((float) i * f);

                if (j < MathHelper.ceiling_float_int((float) i * f) && Math.random() < (double) ((float) i * f - (float) j)) {
                    ++j;
                }

                i = j;
            }

            while (i > 0) {
                final int k = EntityXPOrb.getXPSplit(i);
                i -= k;
                this.thePlayer.worldObj.spawnEntityInWorld(new EntityXPOrb(this.thePlayer.worldObj, this.thePlayer.posX, this.thePlayer.posY + 0.5D, this.thePlayer.posZ + 0.5D, k));
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
