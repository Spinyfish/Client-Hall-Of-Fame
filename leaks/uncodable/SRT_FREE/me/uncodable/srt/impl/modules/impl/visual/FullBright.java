package me.uncodable.srt.impl.modules.impl.visual;

import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;

@ModuleInfo(
   internalName = "FullBright",
   name = "Full Bright",
   desc = "Allows you to see in darkened areas.",
   category = Module.Category.VISUAL,
   legit = true
)
public class FullBright extends Module {
   private float oldBrightness;

   public FullBright(int key, boolean enabled) {
      super(key, enabled);
   }

   @Override
   public void onEnable() {
      this.oldBrightness = MC.gameSettings.gammaSetting;
      MC.gameSettings.gammaSetting = 1000.0F;
   }

   @Override
   public void onDisable() {
      MC.gameSettings.gammaSetting = this.oldBrightness;
   }
}
