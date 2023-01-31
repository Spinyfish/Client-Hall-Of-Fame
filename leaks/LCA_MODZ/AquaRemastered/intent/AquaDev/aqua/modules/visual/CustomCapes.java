package intent.AquaDev.aqua.modules.visual;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import net.minecraft.util.ResourceLocation;

public class CustomCapes extends Module {
   public static ResourceLocation resourceLocation;

   public CustomCapes() {
      super("CustomCapes", Module.Type.Visual, "CustomCapes", 0, Category.Visual);
      Aqua.setmgr.register(new Setting("Mode", this, "Aqua", new String[]{"Aqua", "Anime", "Rias", "Rias2"}));
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
