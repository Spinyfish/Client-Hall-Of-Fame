package me.uncodable.srt.impl.modules.impl.combat;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import me.uncodable.srt.impl.utils.ItemUtils;
import me.uncodable.srt.impl.utils.Timer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemArmor;

@ModuleInfo(
   internalName = "AutoArmor",
   name = "Auto Armor",
   desc = "Allows you to never manually equip armor again.",
   category = Module.Category.COMBAT
)
public class AutoArmor extends Module {
   private static final String INTERNAL_DELAY_VALUE = "INTERNAL_DELAY_VALUE";
   private static final String INTERNAL_OPEN_INVENTORY = "INTERNAL_OPEN_INVENTORY";
   private static final String DELAY_VALUE_SETTING_NAME = "Delay";
   private static final String OPEN_INVENTORY_SETTING_NAME = "On Inventory Open";
   private final Timer timer = new Timer();
   private boolean equipping;

   public AutoArmor(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_DELAY_VALUE", "Delay", 150.0, 0.0, 2000.0, true);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_OPEN_INVENTORY", "On Inventory Open");
   }

   @Override
   public void onDisable() {
      this.timer.reset();
      this.equipping = false;
   }

   @EventTarget(
      target = EventUpdate.class
   )
   public void onUpdate(EventUpdate e) {
      if (e.getState() == EventUpdate.State.PRE) {
         if (this.timer.elapsed((long)Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_DELAY_VALUE", Setting.Type.SLIDER).getCurrentValue())
            && this.equipping) {
            this.equipping = false;
            this.timer.reset();
         }

         if (Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_OPEN_INVENTORY", Setting.Type.CHECKBOX).isTicked()
            && !(MC.currentScreen instanceof GuiContainer)) {
            return;
         }

         for(int i = 9; i < 45 && !this.equipping; ++i) {
            if (MC.thePlayer.inventoryContainer.getSlot(i).getStack() != null
               && MC.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemArmor) {
               switch(ItemUtils.getArmorRating(i)[1]) {
                  case 0:
                     int HELMET_SLOT = 5;
                     if (ItemUtils.getArmorRating(HELMET_SLOT)[0] < ItemUtils.getArmorRating(i)[0] && ItemUtils.getArmorRating(HELMET_SLOT)[1] == 0
                        || ItemUtils.getArmorRating(HELMET_SLOT)[1] == 4) {
                        ItemUtils.swapArmor(HELMET_SLOT, i);
                     }

                     if (ItemUtils.getArmorRating(i)[0] <= ItemUtils.getArmorRating(HELMET_SLOT)[0]
                        || ItemUtils.getArmorRating(i)[1] != ItemUtils.getArmorRating(HELMET_SLOT)[1]) {
                        MC.playerController.windowClick(MC.thePlayer.inventoryContainer.windowId, i, 1, 4, MC.thePlayer);
                     }
                     break;
                  case 1:
                     int CHESTPLATE_SLOT = 6;
                     if (ItemUtils.getArmorRating(CHESTPLATE_SLOT)[0] < ItemUtils.getArmorRating(i)[0] && ItemUtils.getArmorRating(CHESTPLATE_SLOT)[1] == 1
                        || ItemUtils.getArmorRating(CHESTPLATE_SLOT)[1] == 4) {
                        ItemUtils.swapArmor(CHESTPLATE_SLOT, i);
                     }

                     if (ItemUtils.getArmorRating(i)[0] <= ItemUtils.getArmorRating(CHESTPLATE_SLOT)[0]
                        || ItemUtils.getArmorRating(i)[1] != ItemUtils.getArmorRating(CHESTPLATE_SLOT)[1]) {
                        MC.playerController.windowClick(MC.thePlayer.inventoryContainer.windowId, i, 1, 4, MC.thePlayer);
                     }
                     break;
                  case 2:
                     int LEGGINGS_SLOT = 7;
                     if (ItemUtils.getArmorRating(LEGGINGS_SLOT)[0] < ItemUtils.getArmorRating(i)[0]
                           && ItemUtils.getArmorRating(i)[1] == 2
                           && ItemUtils.getArmorRating(LEGGINGS_SLOT)[1] == 2
                        || ItemUtils.getArmorRating(LEGGINGS_SLOT)[1] == 4) {
                        ItemUtils.swapArmor(LEGGINGS_SLOT, i);
                     }

                     if (ItemUtils.getArmorRating(i)[0] <= ItemUtils.getArmorRating(LEGGINGS_SLOT)[0]
                        || ItemUtils.getArmorRating(i)[1] != ItemUtils.getArmorRating(LEGGINGS_SLOT)[1]) {
                        MC.playerController.windowClick(MC.thePlayer.inventoryContainer.windowId, i, 1, 4, MC.thePlayer);
                     }
                     break;
                  case 3:
                     int BOOTS_SLOT = 8;
                     if (ItemUtils.getArmorRating(BOOTS_SLOT)[0] < ItemUtils.getArmorRating(i)[0]
                           && ItemUtils.getArmorRating(i)[1] == 3
                           && ItemUtils.getArmorRating(BOOTS_SLOT)[1] == 3
                        || ItemUtils.getArmorRating(BOOTS_SLOT)[1] == 4) {
                        ItemUtils.swapArmor(BOOTS_SLOT, i);
                     }

                     if (ItemUtils.getArmorRating(i)[0] <= ItemUtils.getArmorRating(BOOTS_SLOT)[0]
                        || ItemUtils.getArmorRating(i)[1] != ItemUtils.getArmorRating(BOOTS_SLOT)[1]) {
                        MC.playerController.windowClick(MC.thePlayer.inventoryContainer.windowId, i, 1, 4, MC.thePlayer);
                     }
               }

               this.equipping = true;
            }
         }
      }
   }

   public Timer getTimer() {
      return this.timer;
   }

   public boolean isEquipping() {
      return this.equipping;
   }
}
