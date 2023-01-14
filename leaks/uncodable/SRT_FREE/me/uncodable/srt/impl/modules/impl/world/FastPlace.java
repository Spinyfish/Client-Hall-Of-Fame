package me.uncodable.srt.impl.modules.impl.world;

import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;

@ModuleInfo(
   internalName = "FastPlace",
   name = "Fast Place",
   desc = "Allows you to place blocks with incredible speed (and endurance).",
   category = Module.Category.WORLD
)
public class FastPlace extends Module {
   public FastPlace(int key, boolean enabled) {
      super(key, enabled);
   }

   @EventTarget(
      target = EventUpdate.class
   )
   public void onUpdate(EventUpdate e) {
      MC.rightClickDelayTimer = 0;
   }
}
