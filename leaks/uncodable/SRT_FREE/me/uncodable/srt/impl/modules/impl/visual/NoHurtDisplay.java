package me.uncodable.srt.impl.modules.impl.visual;

import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;

@ModuleInfo(
   internalName = "NoHurtDisplay",
   name = "No Hurt Display",
   desc = "Allows you to disable the hurt-time display.\nPerfect for PvP.",
   category = Module.Category.VISUAL,
   legit = true
)
public class NoHurtDisplay extends Module {
   public NoHurtDisplay(int key, boolean enabled) {
      super(key, enabled);
   }
}
