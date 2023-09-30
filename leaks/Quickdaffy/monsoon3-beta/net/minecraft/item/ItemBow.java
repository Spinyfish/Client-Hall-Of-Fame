/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemBow
extends Item {
    public static final String[] bowPullIconNameArray = new String[]{"pulling_0", "pulling_1", "pulling_2"};

    public ItemBow() {
        this.maxStackSize = 1;
        this.setMaxDamage(384);
        this.setCreativeTab(CreativeTabs.tabCombat);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft) {
        boolean flag;
        boolean bl = flag = playerIn.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;
        if (flag || playerIn.inventory.hasItem(Items.arrow)) {
            int k;
            int j;
            int i = this.getMaxItemUseDuration(stack) - timeLeft;
            float f = (float)i / 20.0f;
            if ((double)(f = (f * f + f * 2.0f) / 3.0f) < 0.1) {
                return;
            }
            if (f > 1.0f) {
                f = 1.0f;
            }
            EntityArrow entityarrow = new EntityArrow(worldIn, playerIn, f * 2.0f);
            if (f == 1.0f) {
                entityarrow.setIsCritical(true);
            }
            if ((j = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack)) > 0) {
                entityarrow.setDamage(entityarrow.getDamage() + (double)j * 0.5 + 0.5);
            }
            if ((k = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack)) > 0) {
                entityarrow.setKnockbackStrength(k);
            }
            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0) {
                entityarrow.setFire(100);
            }
            stack.damageItem(1, playerIn);
            worldIn.playSoundAtEntity(playerIn, "random.bow", 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + f * 0.5f);
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

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        if (playerIn.capabilities.isCreativeMode || playerIn.inventory.hasItem(Items.arrow)) {
            playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        }
        return itemStackIn;
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }
}

