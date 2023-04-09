package tech.dort.dortware.impl.utils.inventory;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import tech.dort.dortware.api.util.Util;

public class InventoryUtils implements Util {

    public static boolean isValidItem(ItemStack itemStack) {
        if (itemStack.getDisplayName().startsWith("\247a")) {
            return true;
        } else {
            return itemStack.getItem() instanceof ItemArmor || itemStack.getItem() instanceof ItemEnderPearl || itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemTool || itemStack.getItem() instanceof ItemFood || itemStack.getItem() instanceof ItemPotion && !isBadPotion(itemStack) || itemStack.getItem() instanceof ItemBlock || itemStack.getDisplayName().contains("Play") || itemStack.getDisplayName().contains("Game") || itemStack.getDisplayName().contains("Right Click");
        }
    }

    public static float getDamageLevel(ItemStack stack) {
        if (stack.getItem() instanceof ItemSword) {
            ItemSword sword = (ItemSword) stack.getItem();
            float sharpness = (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.field_180314_l.effectId, stack) * 1.25F;
            float fireAspect = (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 1.5F;
            return sword.func_150931_i() + sharpness + fireAspect;
        } else {
            return 0.0F;
        }
    }

    public static boolean isBadPotion(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            ItemPotion potion = (ItemPotion) stack.getItem();
            if (ItemPotion.isSplash(stack.getItemDamage())) {

                for (Object o : potion.getEffects(stack)) {
                    PotionEffect effect = (PotionEffect) o;
                    if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static void swap(int slot, int hotBarNumber) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotBarNumber, 2, mc.thePlayer);
    }
}
