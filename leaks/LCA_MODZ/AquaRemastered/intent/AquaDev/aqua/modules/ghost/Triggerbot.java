package intent.AquaDev.aqua.modules.ghost;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.TimeUtil;
import java.util.Random;
import net.minecraft.util.MathHelper;

public class Triggerbot extends Module {
   TimeUtil timeUtil = new TimeUtil();

   public Triggerbot() {
      super("Triggerbot", Module.Type.Combat, "Triggerbot", 0, Category.Ghost);
   }

   @Override
   public void setup() {
      Aqua.setmgr.register(new Setting("minCPS", this, 8.0, 1.0, 20.0, true));
      Aqua.setmgr.register(new Setting("maxCPS", this, 19.0, 1.0, 20.0, true));
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
   public void onEvent(Event event) {
      if (event instanceof EventUpdate) {
         float minCPS = (float)Aqua.setmgr.getSetting("TriggerbotminCPS").getCurrentNumber();
         float maxCPS = (float)Aqua.setmgr.getSetting("TriggerbotmaxCPS").getCurrentNumber();
         float CPS = (float)MathHelper.getRandomDoubleInRange(new Random(), (double)minCPS, (double)maxCPS);
         if (this.timeUtil.hasReached((long)(1000.0F / CPS)) && mc.objectMouseOver.entityHit != null) {
            mc.clickMouse();
            this.timeUtil.reset();
         }
      }
   }
}
