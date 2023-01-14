package me.uncodable.srt.impl.modules.impl.visual;

import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;

@ModuleInfo(
   internalName = "NoPumpkinOverlay",
   name = "No Pumpkin Overlay",
   desc = "Allows you to see without a blinding pumpkin overlay.",
   category = Module.Category.VISUAL,
   legit = true
)
public class NoPumpkinOverlay extends Module {
   public NoPumpkinOverlay(int key, boolean enabled) {
      super(key, enabled);
   }
}
