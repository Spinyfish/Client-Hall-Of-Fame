package me.uncodable.srt.impl.modules.impl.visual;

import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;

@ModuleInfo(
   internalName = "NoStrike",
   name = "No Strike",
   desc = "Prevents you from being unfairly striked down on YouTube by a shitty, non-existent, and invalid company.",
   category = Module.Category.VISUAL,
   legit = true
)
public class NoStrike extends Module {
   public NoStrike(int key, boolean enabled) {
      super(key, enabled);
   }
}
