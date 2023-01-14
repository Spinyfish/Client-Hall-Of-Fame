package me.uncodable.srt.impl.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemUtils {
   private static final Minecraft MC = Minecraft.getMinecraft();

   public static int[] getArmorRating(int slot) {
      int rating = 0;
      int armorType = 4;
      if (MC.thePlayer.inventoryContainer.getSlot(slot).getStack() != null
         && MC.thePlayer.inventoryContainer.getSlot(slot).getStack().getItem() instanceof ItemArmor) {
         ItemStack stack;
         stack = MC.thePlayer.inventoryContainer.getSlot(slot).getStack();
         ItemArmor armor = (ItemArmor)stack.getItem();
         label46:
         switch(armor.armorType) {
            case 0:
               armorType = 0;
               switch(Item.getIdFromItem(stack.getItem())) {
                  case 298:
                     rating += 25;
                     break label46;
                  case 302:
                     rating += 75;
                     break label46;
                  case 306:
                     rating += 100;
                     break label46;
                  case 310:
                     rating += 125;
                     break label46;
                  case 314:
                     rating += 50;
                  default:
                     break label46;
               }
            case 1:
               armorType = 1;
               switch(Item.getIdFromItem(stack.getItem())) {
                  case 299:
                     rating += 25;
                     break label46;
                  case 303:
                     rating += 75;
                     break label46;
                  case 307:
                     rating += 100;
                     break label46;
                  case 311:
                     rating += 125;
                     break label46;
                  case 315:
                     rating += 50;
                  default:
                     break label46;
               }
            case 2:
               armorType = 2;
               switch(Item.getIdFromItem(stack.getItem())) {
                  case 300:
                     rating += 25;
                     break label46;
                  case 304:
                     rating += 75;
                     break label46;
                  case 308:
                     rating += 100;
                     break label46;
                  case 312:
                     rating += 125;
                     break label46;
                  case 316:
                     rating += 50;
                  default:
                     break label46;
               }
            case 3:
               armorType = 3;
               switch(Item.getIdFromItem(stack.getItem())) {
                  case 301:
                     rating += 25;
                     break;
                  case 305:
                     rating += 75;
                     break;
                  case 309:
                     rating += 100;
                     break;
                  case 313:
                     rating += 125;
                     break;
                  case 317:
                     rating += 50;
               }
         }

         if (stack.isItemEnchanted()) {
            rating += 10;
            rating += EnchantmentHelper.getEnchantmentLevel(0, stack) * 35;
            rating += EnchantmentHelper.getEnchantmentLevel(4, stack) * 10;
            rating += EnchantmentHelper.getEnchantmentLevel(3, stack) * 10;
            rating += EnchantmentHelper.getEnchantmentLevel(1, stack) * 10;
            rating += EnchantmentHelper.getEnchantmentLevel(7, stack) * 10;
            rating += EnchantmentHelper.getEnchantmentLevel(34, stack) * 5;
         }
      }

      return new int[]{rating, armorType};
   }

   public static void swapArmor(int armorSlot, int inventorySlot) {
      MC.playerController.windowClick(MC.thePlayer.inventoryContainer.windowId, armorSlot, 1, 4, MC.thePlayer);
      MC.playerController.windowClick(MC.thePlayer.inventoryContainer.windowId, inventorySlot, 1, 1, MC.thePlayer);
   }
}
