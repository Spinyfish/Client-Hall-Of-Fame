package intent.AquaDev.aqua.modules.combat;

import events.Event;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.TimeUtil;

public class TickBase extends Module {
   public static boolean sneaked;
   TimeUtil timeUtil = new TimeUtil();

   public TickBase() {
      super("TickBase", Module.Type.Combat, "TickBase", 0, Category.Combat);
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
      if (e instanceof EventUpdate) {
         if (mc.gameSettings.keyBindSprint.pressed) {
            sneaked = true;
         } else {
            sneaked = false;
         }

         if (sneaked) {
            mc.timer.timerSpeed = 0.1F;
            this.timeUtil.reset();
         } else {
            mc.timer.timerSpeed = 1.5F;
         }
      }
   }
}
