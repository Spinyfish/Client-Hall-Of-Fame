package intent.AquaDev.aqua.modules.misc;

import events.Event;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;

public class AutoConfig extends Module {
   public AutoConfig() {
      super("AutoConfig", Module.Type.Misc, "AutoConfig", 0, Category.Misc);
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
