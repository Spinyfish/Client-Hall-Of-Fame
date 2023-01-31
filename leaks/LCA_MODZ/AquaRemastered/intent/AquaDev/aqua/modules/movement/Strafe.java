package intent.AquaDev.aqua.modules.movement;

import events.Event;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.PlayerUtil;

public class Strafe extends Module {
   public Strafe() {
      super("Strafe", Module.Type.Movement, "Strafe", 0, Category.Movement);
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @Override
   public void onEvent(Event e) {
      if (e instanceof EventUpdate && !mc.thePlayer.onGround) {
         PlayerUtil.setSpeed(PlayerUtil.getSpeed());
      }
   }
}
