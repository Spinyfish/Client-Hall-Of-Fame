package intent.AquaDev.aqua.modules.combat;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.TimeUtil;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.MathHelper;

public class AntiFireball extends Module {
   public static EntityFireball target = null;
   public TimeUtil timeUtil = new TimeUtil();

   public AntiFireball() {
      super("AntiFireball", Module.Type.Combat, "AntiFireball", 0, Category.Combat);
      Aqua.setmgr.register(new Setting("Range", this, 6.0, 3.0, 6.0, false));
      Aqua.setmgr.register(new Setting("minCPS", this, 17.0, 1.0, 20.0, false));
      Aqua.setmgr.register(new Setting("maxCPS", this, 19.0, 1.0, 20.0, false));
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.timeUtil.reset();
      super.onDisable();
   }

   @Override
   public void onEvent(Event event) {
      if (event instanceof EventUpdate) {
         float minCPS = (float)Aqua.setmgr.getSetting("AntiFireballminCPS").getCurrentNumber();
         float maxCPS = (float)Aqua.setmgr.getSetting("AntiFireballmaxCPS").getCurrentNumber();
         float CPS = (float)MathHelper.getRandomDoubleInRange(new Random(), (double)minCPS, (double)maxCPS);
         target = this.searchTargets();
         if (target != null && Killaura.target == null && this.timeUtil.hasReached((long)(1000.0F / CPS))) {
            mc.thePlayer.swingItem();
            mc.playerController.attackEntity(mc.thePlayer, target);
            this.timeUtil.reset();
         }
      }
   }

   public EntityFireball searchTargets() {
      float range = (float)Aqua.setmgr.getSetting("AntiFireballRange").getCurrentNumber();
      EntityFireball player = null;
      double closestDist = 100000.0;

      for(Entity o : mc.theWorld.loadedEntityList) {
         if (!o.getName().equals(mc.thePlayer.getName()) && o instanceof EntityFireball && mc.thePlayer.getDistanceToEntity(o) < range) {
            double dist = (double)mc.thePlayer.getDistanceToEntity(o);
            if (dist < closestDist) {
               closestDist = dist;
               player = (EntityFireball)o;
            }
         }
      }

      return player;
   }
}
