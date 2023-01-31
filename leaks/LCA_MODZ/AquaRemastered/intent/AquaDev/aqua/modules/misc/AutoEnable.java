package intent.AquaDev.aqua.modules.misc;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;

public class AutoEnable extends Module {
   public AutoEnable() {
      super("AutoEnable", Module.Type.Misc, "AutoEnable", 0, Category.Misc);
      Aqua.setmgr.register(new Setting("Velocity", this, false));
      Aqua.setmgr.register(new Setting("Blur", this, true));
      Aqua.setmgr.register(new Setting("Shadow", this, true));
      Aqua.setmgr.register(new Setting("Arraylist", this, true));
      Aqua.setmgr.register(new Setting("ShaderMultiplier", this, true));
      Aqua.setmgr.register(new Setting("ShaderESP", this, true));
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
