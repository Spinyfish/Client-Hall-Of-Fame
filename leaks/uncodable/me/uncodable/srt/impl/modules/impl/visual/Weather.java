package me.uncodable.srt.impl.modules.impl.visual;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.render.EventWeatherRender;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;

@ModuleInfo(
   internalName = "Weather",
   name = "Weather",
   desc = "Allows you to change the weather on demand.\nGod uses this cheat on the daily.",
   category = Module.Category.VISUAL,
   legit = true
)
public class Weather extends Module {
   private EventWeatherRender event;
   private float oldStrength;
   private boolean set;
   private static final String COMBO_BOX_SETTING_NAME = "Weather";
   private static final String CLEAR = "Clear";
   private static final String RAIN = "Rain";
   private static final String THUNDER = "Thunder";

   public Weather(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addComboBox(this, "INTERNAL_GENERAL_COMBO_BOX", "Weather", "Clear", "Rain", "Thunder");
   }

   @Override
   public void onDisable() {
      this.event.setRainStrength(this.oldStrength);
   }

   @EventTarget(
      target = EventWeatherRender.class
   )
   public void onWeatherRender(EventWeatherRender e) {
      if (!this.set) {
         this.oldStrength = e.getRainStrength();
         this.set = true;
      }

      String var2 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
      switch(var2) {
         case "Clear":
            e.setRainStrength(0.0F);
            break;
         case "Rain":
            e.setRainStrength(1.0F);
            break;
         case "Thunder":
            e.setRainStrength(2.0F);
      }

      this.event = e;
   }
}
