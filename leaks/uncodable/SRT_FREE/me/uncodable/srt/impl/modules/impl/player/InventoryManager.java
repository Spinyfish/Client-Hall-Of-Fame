package me.uncodable.srt.impl.modules.impl.player;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import me.uncodable.srt.impl.modules.impl.combat.AutoArmor;
import me.uncodable.srt.impl.utils.Timer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

@ModuleInfo(
   internalName = "InventoryManager",
   name = "Inventory Manager",
   desc = "Allows the client to automatically clean up your inventory for you.",
   category = Module.Category.PLAYER
)
public class InventoryManager extends Module {
   private static final String INTERNAL_DELAY_VALUE = "INTERNAL_DELAY_VALUE";
   private static final String INTERNAL_OPEN_INVENTORY = "INTERNAL_OPEN_INVENTORY";
   private static final String INTERNAL_BLOCK_COUNT_VALUE = "INTERNAL_BLOCK_COUNT_VALUE";
   private static final String DELAY_VALUE_SETTING_NAME = "Delay";
   private static final String COMBO_BOX_SETTING_NAME = "Manager Mode";
   private static final String OPEN_INVENTORY_SETTING_NAME = "On Inventory Open";
   private static final String BLOCK_COUNT_VALUE_SETTING_NAME = "Block Count";
   private static final String MINIGAME = "Mini-Game";
   private final Timer timer = new Timer();
   private boolean cleaning;

   public InventoryManager(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addComboBox(this, "INTERNAL_GENERAL_COMBO_BOX", "Manager Mode", "Mini-Game");
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_DELAY_VALUE", "Delay", 150.0, 0.0, 2000.0, true);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_BLOCK_COUNT_VALUE", "Block Count", 128.0, 0.0, 300.0, true);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_OPEN_INVENTORY", "On Inventory Open");
   }

   @Override
   public void onDisable() {
      this.timer.reset();
      this.cleaning = false;
   }

   @EventTarget(
      target = EventUpdate.class
   )
   public void onUpdate(EventUpdate e) {
      if (e.getState() == EventUpdate.State.PRE && !((AutoArmor)Ries.INSTANCE.getModuleManager().getModuleByName("AutoArmor")).isEquipping()) {
         int blockCount = 0;
         if (this.timer.elapsed((long)Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_DELAY_VALUE", Setting.Type.SLIDER).getCurrentValue())
            && this.cleaning) {
            this.cleaning = false;
            this.timer.reset();
         }

         if (Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_OPEN_INVENTORY", Setting.Type.CHECKBOX).isTicked()
            && !(MC.currentScreen instanceof GuiContainer)) {
            return;
         }

         String var5 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
         switch(var5) {
            case "Mini-Game":
               for(int i = 9; i < 45 && !this.cleaning; ++i) {
                  ItemStack stack = MC.thePlayer.inventoryContainer.getSlot(i).getStack();
                  if (stack != null) {
                     Item item = stack.getItem();
                     if (item == Item.getItemFromBlock(Blocks.planks)
                        || item == Item.getItemFromBlock(Blocks.stone)
                        || item == Item.getItemFromBlock(Blocks.wool)) {
                        blockCount += stack.stackSize;
                     }

                     if (this.shouldCleanMiniGame(item, blockCount)) {
                        MC.playerController.windowClick(MC.thePlayer.inventoryContainer.windowId, i, 1, 4, MC.thePlayer);
                        this.cleaning = true;
                     }
                  }
               }
         }
      }
   }

   private boolean shouldCleanMiniGame(Item item, int blockCount) {
      return !(item instanceof ItemSword)
         && !(item instanceof ItemAxe)
         && !(item instanceof ItemPotion)
         && !(item instanceof ItemArmor)
         && !(item instanceof ItemFood)
         && item != Items.bow
         && item != Items.arrow
         && item != Items.ender_pearl
         && (
            !((double)blockCount <= Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_BLOCK_COUNT_VALUE", Setting.Type.SLIDER).getCurrentValue())
               || item != Item.getItemFromBlock(Blocks.planks) && item != Item.getItemFromBlock(Blocks.stone) && item != Item.getItemFromBlock(Blocks.wool)
         );
   }
}
