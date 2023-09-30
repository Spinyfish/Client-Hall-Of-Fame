package dev.eternal.client.util.inventory;

import lombok.experimental.UtilityClass;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@UtilityClass
public class InventoryUtil {
  public boolean isValidItem(ItemStack itemStack) {
    if (itemStack.getDisplayName().startsWith("\247a")) {
      return true;
    } else {
      return itemStack.getItem() instanceof ItemArmor ||
          itemStack.getItem() instanceof ItemEnderPearl ||
          itemStack.getItem() instanceof ItemSword ||
          itemStack.getItem() instanceof ItemTool ||
          itemStack.getItem() instanceof ItemFood ||
          itemStack.getItem() instanceof ItemPotion &&
              !isBadPotion(itemStack) ||
          itemStack.getItem() instanceof ItemBlock ||
          itemStack.getDisplayName().contains("Play") ||
          itemStack.getDisplayName().contains("Game") ||
          itemStack.getDisplayName().contains("Right Click");
    }
  }

  public boolean isBadPotion(ItemStack stack) {
    if (stack != null && stack.getItem() instanceof ItemPotion potion) {
      if (ItemPotion.isSplash(stack.getItemDamage())) {

        for (PotionEffect potionEffect : potion.getEffects(stack)) {
          if (potionEffect.getPotionID() == Potion.poison.getId() ||
              potionEffect.getPotionID() == Potion.harm.getId() ||
              potionEffect.getPotionID() == Potion.moveSlowdown.getId() ||
              potionEffect.getPotionID() == Potion.weakness.getId()) {
            return true;
          }
        }
      }
    }

    return false;
  }

  public float getDamageLevel(ItemStack stack) {
    if (stack.getItem() instanceof ItemSword sword) {
      float sharpness = (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F;
      float fireAspect = (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 1.5F;
      return sword.getDamageVsEntity() + sharpness + fireAspect;
    } else {
      return 0.0F;
    }
  }
}
