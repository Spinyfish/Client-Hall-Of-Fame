package intent.AquaDev.aqua.modules.visual;

import events.Event;
import events.listeners.EventPreMotion;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class Fullbright extends Module {
   public Fullbright() {
      super("Fullbright", Module.Type.Visual, "Fullbright", 0, Category.Visual);
      System.out.println("Sprint::init");
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      mc.thePlayer.removePotionEffect(Potion.nightVision.getId());
      super.onDisable();
   }

   @Override
   public void onEvent(Event e) {
      if (e instanceof EventPreMotion) {
         mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), Integer.MAX_VALUE));
      }
   }
}
