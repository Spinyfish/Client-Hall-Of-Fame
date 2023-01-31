package intent.AquaDev.aqua.modules.misc;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;

public class Configs extends Module {
   public Configs() {
      super("Configs", Module.Type.Misc, "Configs", 0, Category.Misc);
      Aqua.setmgr.register(new Setting("LoadVisuals", this, true));
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
