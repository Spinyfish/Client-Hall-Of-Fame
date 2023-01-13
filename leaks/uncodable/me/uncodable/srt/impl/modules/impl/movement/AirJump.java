package me.uncodable.srt.impl.modules.impl.movement;

import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;

@ModuleInfo(
   internalName = "AirJump",
   name = "Air Jump",
   desc = "Allows you to jump while in mid-air.\nAlso can be used as a step for some anti-cheats.",
   category = Module.Category.MOVEMENT
)
public class AirJump extends Module {
   public AirJump(int key, boolean enabled) {
      super(key, enabled);
   }

   @EventTarget(
      target = EventMotionUpdate.class
   )
   public void onMotion(EventMotionUpdate e) {
      if (MC.gameSettings.keyBindJump.isPressed()) {
         MC.thePlayer.jump();
      }
   }
}
