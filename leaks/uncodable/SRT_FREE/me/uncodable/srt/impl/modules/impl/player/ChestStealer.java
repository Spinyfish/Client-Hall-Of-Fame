package me.uncodable.srt.impl.modules.impl.player;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import me.uncodable.srt.impl.utils.Timer;
import net.minecraft.inventory.ContainerChest;

@ModuleInfo(
   internalName = "ChestStealer",
   name = "Chest Stealer",
   desc = "Allows you to steal chests with incredible speed.\nAlso allows for mass amounts of robberies worthy of a felony!",
   category = Module.Category.PLAYER
)
public class ChestStealer extends Module {
   private static final String INTERNAL_DELAY_VALUE = "INTERNAL_DELAY_VALUE";
   private static final String DELAY_VALUE_SETTING_NAME = "Steal Delay";
   private final Timer timer = new Timer();

   public ChestStealer(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_DELAY_VALUE", "Steal Delay", 0.0, 0.0, 1000.0, true);
   }

   @EventTarget(
      target = EventUpdate.class
   )
   public void onUpdate(EventUpdate e) {
      if (e.getState() == EventUpdate.State.PRE && MC.thePlayer.openContainer instanceof ContainerChest) {
         ContainerChest container = (ContainerChest)MC.thePlayer.openContainer;
         double delay = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_DELAY_VALUE", Setting.Type.SLIDER).getCurrentValue();

         for(int i = 0; i < container.getLowerChestInventory().getSizeInventory(); ++i) {
            if (container.getLowerChestInventory().getStackInSlot(i) != null && this.timer.elapsed((long)delay)) {
               MC.playerController.windowClick(container.windowId, i, 0, 1, MC.thePlayer);
               this.timer.reset();
            }
         }
      }
   }
}
