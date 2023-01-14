package me.uncodable.srt.impl.modules.impl.miscellaneous;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;

@ModuleInfo(
   internalName = "Timer",
   name = "Timer",
   desc = "Allows you to change the general speed of the game.",
   category = Module.Category.MISCELLANEOUS
)
public class Timer extends Module {
   private float oldGameSpeed;
   private static final String INTERNAL_GAMESPEED_VALUE = "INTERNAL_GAMESPEED_VALUE";
   private static final String GAMESPEED_VALUE_SETTING_NAME = "Game Speed";

   public Timer(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_GAMESPEED_VALUE", "Game Speed", 2.0, 0.01, 20.0);
   }

   @Override
   public void onEnable() {
      this.oldGameSpeed = MC.timer.getTimerSpeed();
   }

   @Override
   public void onDisable() {
      MC.timer.setTimerSpeed(this.oldGameSpeed);
   }

   @EventTarget(
      target = EventMotionUpdate.class
   )
   public void onMotion(EventMotionUpdate e) {
      double gameSpeed = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GAMESPEED_VALUE", Setting.Type.SLIDER).getCurrentValue();
      if (e.getState() == EventMotionUpdate.State.PRE && (double)MC.timer.getTimerSpeed() != gameSpeed) {
         MC.timer.setTimerSpeed((float)gameSpeed);
      }
   }
}
