package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemBow extends Item {
    public static final String[] bowPullIconNameArray = new String[]{"pulling_0", "pulling_1", "pulling_2"};

    public ItemBow() {
        this.maxStackSize = 1;
        this.setMaxDamage(384);
        this.setCreativeTab(CreativeTabs.tabCombat);
    }

    /**
     * Called when the player stops using an Item (stops holding the right mouse button).
     *
     * @param timeLeft The amount of ticks left before the using would have been complete
     */
    public void onPlayerStoppedUsing(final ItemStack stack, final World worldIn, final EntityPlayer playerIn, final int timeLeft) {
        final boolean flag = playerIn.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;

        if (flag || playerIn.inventory.hasItem(Items.arrow)) {
            final int i = this.getMaxItemUseDuration(stack) - timeLeft;
            float f = (float) i / 20.0F;
            f = (f * f + f * 2.0F) / 3.0F;

            if ((double) f < 0.1D) {
                return;
            }

            if (f > 1.0F) {
                f = 1.0F;
            }

            final EntityArrow entityarrow = new EntityArrow(worldIn, playerIn, f * 2.0F);

            if (f == 1.0F) {
                entityarrow.setIsCritical(true);
            }

            final int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

            if (j > 0) {
                entityarrow.setDamage(entityarrow.getDamage() + (double) j * 0.5D + 0.5D);
            }

            final int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);

            if (k > 0) {
                entityarrow.setKnockbackStrength(k);
            }

            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0) {
                entityarrow.setFire(100);
            }

            stack.damageItem(1, playerIn);
            worldIn.playSoundAtEntity(playerIn, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

            if (flag) {
                entityarrow.canBePickedUp = 2;
            } else {
                playerIn.inventory.consumeInventoryItem(Items.arrow);
            }

            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);

            if (!worldIn.isRemote) {
                worldIn.spawnEntityInWorld(entityarrow);
            }
        }
    }

    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    public ItemStack onItemUseFinish(final ItemStack stack, final World worldIn, final EntityPlayer playerIn) {
        return stack;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(final ItemStack stack) {
        return 72000;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(final ItemStack stack) {
        return EnumAction.BOW;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(final ItemStack itemStackIn, final World worldIn, final EntityPlayer playerIn) {
        if (playerIn.capabilities.isCreativeMode || playerIn.inventory.hasItem(Items.arrow)) {
            playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        }

        return itemStackIn;
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int getItemEnchantability() {
        return 1;
    }
}
