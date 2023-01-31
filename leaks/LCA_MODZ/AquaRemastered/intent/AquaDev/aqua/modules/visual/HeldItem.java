package intent.AquaDev.aqua.modules.visual;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;

public class HeldItem extends Module {
   public static float hitSpeed = 6.0F;
   public static float scale;
   public static float x;
   public static float y;
   public static float z;

   public HeldItem() {
      super("HeldItem", Module.Type.Visual, "HeldItem", 0, Category.Visual);
      Aqua.setmgr.register(new Setting("Speed", this, 6.0, 1.0, 30.0, false));
      Aqua.setmgr.register(new Setting("Scale", this, 0.4, 0.1, 1.0, false));
      Aqua.setmgr.register(new Setting("X", this, -0.4, -0.1, 1.0, false));
      Aqua.setmgr.register(new Setting("Y", this, 0.2F, 0.1, 1.0, false));
      Aqua.setmgr.register(new Setting("Z", this, -0.2F, -0.1, 1.0, false));
      System.out.println("HeldItem::init");
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
