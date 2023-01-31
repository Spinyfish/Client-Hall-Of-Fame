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
import org.lwjgl.input.Mouse;

public class AutoClicker extends Module {
   TimeUtil timeUtil = new TimeUtil();
   public boolean attack;

   public AutoClicker() {
      super("AutoClicker", Module.Type.Combat, "AutoClicker", 0, Category.Ghost);
      System.out.println("AutoClicker::init");
   }

   @Override
   public void setup() {
      Aqua.setmgr.register(new Setting("OnlyLeftClick", this, true));
      Aqua.setmgr.register(new Setting("1.9", this, false));
      Aqua.setmgr.register(new Setting("minCPS", this, 8.0, 1.0, 20.0, true));
      Aqua.setmgr.register(new Setting("maxCPS", this, 19.0, 1.0, 20.0, true));
   }

   @Override
   public void onEnable() {
      this.attack = false;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.attack = false;
      super.onDisable();
   }

   @Override
   public void onEvent(Event event) {
      if (event instanceof EventUpdate) {
         float minCPS = (float)Aqua.setmgr.getSetting("AutoClickerminCPS").getCurrentNumber();
         float maxCPS = (float)Aqua.setmgr.getSetting("AutoClickermaxCPS").getCurrentNumber();
         float CPS = (float)MathHelper.getRandomDoubleInRange(new Random(), (double)minCPS, (double)maxCPS);
         mc.gameSettings.keyBindAttack.pressed = false;
         if (Mouse.isButtonDown(0)) {
            this.attack = true;
         } else {
            this.attack = false;
         }

         if (!Aqua.setmgr.getSetting("AutoClicker1.9").isState()) {
            if (this.timeUtil.hasReached((long)(1000.0F / CPS))) {
               if (Aqua.setmgr.getSetting("AutoClickerOnlyLeftClick").isState()) {
                  if (this.attack) {
                     mc.clickMouse();
                  }
               } else {
                  mc.clickMouse();
               }

               this.timeUtil.reset();
            }
         } else if (Aqua.setmgr.getSetting("AutoClicker1.9").isState()) {
            if (Aqua.setmgr.getSetting("AutoClickerOnlyLeftClick").isState()) {
               if (this.attack && mc.thePlayer.ticksExisted % 11 == 0) {
                  mc.clickMouse();
               }
            } else if (mc.thePlayer.ticksExisted % 11 == 0) {
               mc.clickMouse();
            }
         }
      }
   }
}
