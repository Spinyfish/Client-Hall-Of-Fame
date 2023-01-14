package me.uncodable.srt.impl.modules.impl.player;

import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventUpdate;
import me.uncodable.srt.impl.modules.api.Module;

public class Zoot extends Module {
   public Zoot(int key, boolean enabled) {
      super(key, enabled);
   }

   @EventTarget(
      target = EventUpdate.class
   )
   public void onUpdate(EventUpdate e) {
   }
}
