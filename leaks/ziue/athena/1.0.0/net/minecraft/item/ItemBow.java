package net.minecraft.item;

import net.minecraft.creativetab.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.enchantment.*;
import net.minecraft.init.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.*;
import net.minecraft.stats.*;

public class ItemBow extends Item
{
    public static final String[] bowPullIconNameArray;
    
    public ItemBow() {
        this.maxStackSize = 1;
        this.setMaxDamage(384);
        this.setCreativeTab(CreativeTabs.tabCombat);
    }
    
    @Override
    public void onPlayerStoppedUsing(final ItemStack stack, final World worldIn, final EntityPlayer playerIn, final int timeLeft) {
        final boolean flag = playerIn.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;
        if (flag || playerIn.inventory.hasItem(Items.arrow)) {
            final int i = this.getMaxItemUseDuration(stack) - timeLeft;
            float f = i / 20.0f;
            f = (f * f + f * 2.0f) / 3.0f;
            if (f < 0.1) {
                return;
            }
            if (f > 1.0f) {
                f = 1.0f;
            }
            final EntityArrow entityarrow = new EntityArrow(worldIn, playerIn, f * 2.0f);
            if (f == 1.0f) {
                entityarrow.setIsCritical(true);
            }
            final int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
            if (j > 0) {
                entityarrow.setDamage(entityarrow.getDamage() + j * 0.5 + 0.5);
            }
            final int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
            if (k > 0) {
                entityarrow.setKnockbackStrength(k);
            }
            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0) {
                entityarrow.setFire(100);
            }
            stack.damageItem(1, playerIn);
            worldIn.playSoundAtEntity(playerIn, "random.bow", 1.0f, 1.0f / (ItemBow.itemRand.nextFloat() * 0.4f + 1.2f) + f * 0.5f);
            if (flag) {
                entityarrow.canBePickedUp = 2;
            }
            else {
                playerIn.inventory.consumeInventoryItem(Items.arrow);
            }
            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
            if (!worldIn.isRemote) {
                worldIn.spawnEntityInWorld(entityarrow);
            }
        }
    }
    
    @Override
    public ItemStack onItemUseFinish(final ItemStack stack, final World worldIn, final EntityPlayer playerIn) {
        return stack;
    }
    
    @Override
    public int getMaxItemUseDuration(final ItemStack stack) {
        return 72000;
    }
    
    @Override
    public EnumAction getItemUseAction(final ItemStack stack) {
        return EnumAction.BOW;
    }
    
    @Override
    public ItemStack onItemRightClick(final ItemStack itemStackIn, final World worldIn, final EntityPlayer playerIn) {
        if (playerIn.capabilities.isCreativeMode || playerIn.inventory.hasItem(Items.arrow)) {
            playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        }
        return itemStackIn;
    }
    
    @Override
    public int getItemEnchantability() {
        return 1;
    }
    
    static {
        bowPullIconNameArray = new String[] { "pulling_0", "pulling_1", "pulling_2" };
    }
}
