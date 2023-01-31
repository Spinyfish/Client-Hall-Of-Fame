package intent.AquaDev.aqua.modules.player;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.TimeUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class ChestStealer extends Module {
   TimeUtil time = new TimeUtil();

   public ChestStealer() {
      super("ChestStealer", Module.Type.Player, "ChestStealer", 0, Category.Player);
      Aqua.setmgr.register(new Setting("Delay", this, 50.0, 0.0, 1000.0, false));
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
   public void onEvent(Event event) {
      if (event instanceof EventUpdate) {
         float delay = (float)Aqua.setmgr.getSetting("ChestStealerDelay").getCurrentNumber();
         if (mc.thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest container = (ContainerChest)mc.thePlayer.openContainer;

            for(int i = 0; i < container.getLowerChestInventory().getSizeInventory(); ++i) {
               if (container.getLowerChestInventory().getStackInSlot(i) != null && this.time.hasReached((long)delay)) {
                  mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
                  this.time.reset();
               }
            }

            GuiChest chest = (GuiChest)mc.currentScreen;
            if (this.isChestEmpty(chest) || this.isInventoryFull()) {
               mc.thePlayer.closeScreen();
            }
         }
      }
   }

   private boolean isChestEmpty(GuiChest chest) {
      for(int index = 0; index < chest.getLowerChestInventory().getSizeInventory(); ++index) {
         ItemStack stack = chest.getLowerChestInventory().getStackInSlot(index);
         if (stack != null && this.isValidItem(stack)) {
            return false;
         }
      }

      return true;
   }

   private boolean isInventoryFull() {
      for(int index = 9; index <= 44; ++index) {
         ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
         if (stack == null) {
            return false;
         }
      }

      return true;
   }

   private boolean isValidItem(ItemStack itemStack) {
      return itemStack.getItem() instanceof ItemArmor
         || itemStack.getItem() instanceof ItemSword
         || itemStack.getItem() instanceof ItemTool
         || itemStack.getItem() instanceof ItemFood
         || itemStack.getItem() instanceof ItemPotion
         || itemStack.getItem() instanceof ItemBlock;
   }
}
