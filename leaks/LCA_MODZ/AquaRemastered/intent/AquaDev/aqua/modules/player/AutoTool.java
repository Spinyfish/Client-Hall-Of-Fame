package intent.AquaDev.aqua.modules.player;

import events.Event;
import events.listeners.EventClickMouse;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.modules.combat.BedAura;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.MovingObjectPosition;

public class AutoTool extends Module {
   private int slot = 0;

   public AutoTool() {
      super("AutoTool", Module.Type.Player, "AutoTool", 0, Category.Player);
      System.out.println("Sprint::init");
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @Override
   public void onEvent(Event e) {
      if (e instanceof EventUpdate && !BedAura.rotating) {
         double lastSpeed = 0.0;
         this.slot = mc.thePlayer.inventory.currentItem;
         if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            for(int i = 36; i < mc.thePlayer.inventoryContainer.inventorySlots.size(); ++i) {
               ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
               if (itemStack != null) {
                  Item item = itemStack.getItem();
                  if (mc.gameSettings.keyBindAttack.isKeyDown() && (item instanceof ItemTool || item instanceof ItemSword)) {
                     double toolSpeed = this.getToolSpeed(itemStack);
                     double currentSpeed = this.getToolSpeed(mc.thePlayer.getHeldItem());
                     if (toolSpeed > 1.0 && toolSpeed > currentSpeed && toolSpeed > lastSpeed) {
                        this.slot = i - 36;
                        lastSpeed = toolSpeed;
                     }
                  }
               }
            }
         }
      }

      if (e instanceof EventClickMouse && !BedAura.rotating) {
         ((EventClickMouse)e).setSlot(this.slot);
      }
   }

   private double getToolSpeed(ItemStack itemStack) {
      double damage = 0.0;
      if (itemStack != null && (itemStack.getItem() instanceof ItemTool || itemStack.getItem() instanceof ItemSword)) {
         if (itemStack.getItem() instanceof ItemAxe) {
            damage += (double)(
               itemStack.getItem().getStrVsBlock(itemStack, mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock())
                  + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack)
            );
         } else if (itemStack.getItem() instanceof ItemPickaxe) {
            damage += (double)(
               itemStack.getItem().getStrVsBlock(itemStack, mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock())
                  + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack)
            );
         } else if (itemStack.getItem() instanceof ItemSpade) {
            damage += (double)(
               itemStack.getItem().getStrVsBlock(itemStack, mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock())
                  + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack)
            );
         } else if (itemStack.getItem() instanceof ItemSword) {
            damage += (double)itemStack.getItem().getStrVsBlock(itemStack, mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock());
         }

         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) / 11.0;
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, itemStack) / 11.0;
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) / 33.0;
         return damage - (double)itemStack.getItemDamage() / 10000.0;
      } else {
         return 0.0;
      }
   }
}
