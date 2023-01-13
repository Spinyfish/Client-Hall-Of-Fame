package me.uncodable.srt.impl.modules.impl.miscellaneous;

import java.util.Calendar;
import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;

@ModuleInfo(
   internalName = "TimeChecker",
   name = "Time Checker",
   desc = "Allows SRT to send you an update on the time.\nPerfect for looking back at the days that were actually enjoyable.",
   category = Module.Category.MISCELLANEOUS,
   legit = true
)
public class TimeChecker extends Module {
   private static final String INTERNAL_DELAY_VALUE = "INTERNAL_DELAY_VALUE";
   private static final String DELAY_VALUE_SETTING_NAME = "Message Delay";
   private final me.uncodable.srt.impl.utils.Timer timer = new me.uncodable.srt.impl.utils.Timer();

   public TimeChecker(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_DELAY_VALUE", "Message Delay", 1.0, 0.0, 5.0, true);
   }

   @Override
   public void onDisable() {
      this.timer.reset();
   }

   @EventTarget(
      target = EventUpdate.class
   )
   public void onUpdate(EventUpdate e) {
      if (this.timer.elapsed((long)Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_DELAY_VALUE", Setting.Type.SLIDER).getCurrentValue() * 60000L)
         )
       {
         Ries.INSTANCE.msg("Current Time: " + Calendar.getInstance().getTime());
         this.timer.reset();
      }
   }
}
