package me.uncodable.srt.impl.modules.impl.visual;

import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;

@ModuleInfo(
   internalName = "LowFire",
   name = "Low Fire",
   desc = "Allows you to see through the eye-cancer flames.",
   category = Module.Category.VISUAL,
   legit = true
)
public class LowFire extends Module {
   public LowFire(int key, boolean enabled) {
      super(key, enabled);
   }
}
