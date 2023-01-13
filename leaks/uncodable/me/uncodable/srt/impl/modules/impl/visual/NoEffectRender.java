package me.uncodable.srt.impl.modules.impl.visual;

import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;

@ModuleInfo(
   internalName = "NoEffectRender",
   name = "No Effect Render",
   desc = "Allows you to negate every visual potion effect.\nNo more blindness that's a bigger turn off than transgenders!",
   category = Module.Category.VISUAL
)
public class NoEffectRender extends Module {
   public NoEffectRender(int key, boolean enabled) {
      super(key, enabled);
   }
}
