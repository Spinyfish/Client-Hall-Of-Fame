package me.uncodable.srt.impl.modules.impl.miscellaneous;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;

@ModuleInfo(
   internalName = "AutoTip",
   name = "Auto Tip",
   desc = "Allows you to automatically tip all players on the Hypixel Network.\nDon't you dare enable this on any other server...",
   category = Module.Category.MISCELLANEOUS,
   legit = true
)
public class AutoTip extends Module {
   private static final String INTERNAL_DELAY_VALUE = "INTERNAL_DELAY_VALUE";
   private static final String DELAY_VALUE_SETTING_NAME = "Delay";
   private final me.uncodable.srt.impl.utils.Timer timer = new me.uncodable.srt.impl.utils.Timer();

   public AutoTip(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_DELAY_VALUE", "Delay", 1.0, 0.0, 5.0, true);
   }

   @Override
   public void onDisable() {
      this.timer.reset();
   }

   @EventTarget(
      target = EventUpdate.class
   )
   public void onUpdate(EventUpdate e) {
      if (this.timer
         .elapsed((long)(60000.0 * Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_DELAY_VALUE", Setting.Type.SLIDER).getCurrentValue()))) {
         if (MC.getCurrentServerData() != null && MC.getCurrentServerData().serverIP.toLowerCase().contains("hypixel.net")) {
            MC.thePlayer.sendChatMessage("/tip all");
            Ries.INSTANCE.msg("Tipped everyone on the server.");
         } else {
            Ries.INSTANCE.msg("This module is designed for the Hypixel Network.");
         }

         this.timer.reset();
      }
   }
}
