package intent.AquaDev.aqua.modules.visual;

import events.Event;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;

public class NoHurtCam extends Module {
   public NoHurtCam() {
      super("NoHurtCam", Module.Type.Visual, "NoHurtCam", 0, Category.Visual);
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
      }
   }
}
