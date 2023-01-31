package intent.AquaDev.aqua.modules.combat;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

public class TimerRange extends Module {
   public static EntityPlayer target = null;
   String inRange;

   public TimerRange() {
      super("TimerRange", Module.Type.Combat, "TimerRange", 0, Category.Combat);
      Aqua.setmgr.register(new Setting("Range", this, 3.0, 3.0, 6.0, false));
      Aqua.setmgr.register(new Setting("Boost", this, 12.0, 1.0, 50.0, false));
      Aqua.setmgr.register(new Setting("OnHitTPTicks", this, 18.0, 0.0, 100.0, false));
      Aqua.setmgr.register(new Setting("OnHitTP", this, false));
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      mc.timer.timerSpeed = 1.0F;
      super.onDisable();
   }

   @Override
   public void onEvent(Event e) {
      if (e instanceof EventUpdate) {
         if (!Aqua.setmgr.getSetting("TimerRangeOnHitTP").isState()) {
            if (Aqua.moduleManager.getModuleByName("Killaura").isToggled()) {
               if (target == null) {
                  this.inRange = "out";
               }

               if (target != null) {
                  this.inRange = "in";
               }

               target = this.searchTargets();
               if (target != null && Objects.equals(this.inRange, "out")) {
                  this.inRange = "teleport";
               }

               if (target == null && Objects.equals(this.inRange, "in")) {
                  this.inRange = "slow";
               }

               if (Objects.equals(this.inRange, "teleport")) {
                  float boost = (float)Aqua.setmgr.getSetting("TimerRangeBoost").getCurrentNumber();
                  mc.timer.timerSpeed = boost;
               } else if (Objects.equals(this.inRange, "slow")) {
                  mc.timer.timerSpeed = 0.2F;
               } else {
                  mc.timer.timerSpeed = 1.0F;
               }
            } else {
               mc.timer.timerSpeed = 1.0F;
            }
         } else if (Aqua.moduleManager.getModuleByName("Killaura").isToggled()) {
            if (target == null) {
               this.inRange = "out";
            }

            if (target != null) {
               this.inRange = "in";
            }

            target = Killaura.target;
            if (target != null && Objects.equals(this.inRange, "out")) {
               this.inRange = "teleport";
            }

            if (target == null && Objects.equals(this.inRange, "in")) {
               this.inRange = "slow";
            }

            if (Killaura.target != null) {
               float boostTicks = (float)Aqua.setmgr.getSetting("TimerRangeOnHitTPTicks").getCurrentNumber();
               if (!Objects.equals(this.inRange, "teleport")
                  && (
                     Killaura.target.hurtTime == 0
                        || (float)mc.thePlayer.ticksExisted % boostTicks != 0.0F
                        || mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null
                  )) {
                  if (Objects.equals(this.inRange, "slow")) {
                     mc.timer.timerSpeed = 0.2F;
                  } else {
                     mc.timer.timerSpeed = 1.0F;
                  }
               } else {
                  float boost = (float)Aqua.setmgr.getSetting("TimerRangeBoost").getCurrentNumber();
                  mc.timer.timerSpeed = boost;
               }
            } else {
               mc.timer.timerSpeed = 1.0F;
            }
         } else {
            mc.timer.timerSpeed = 1.0F;
         }
      }
   }

   public EntityPlayer searchTargets() {
      float range = (float)Aqua.setmgr.getSetting("TimerRangeRange").getCurrentNumber();
      EntityPlayer player = null;
      double closestDist = 100000.0;

      for(Entity o : mc.theWorld.loadedEntityList) {
         if (!o.getName().equals(mc.thePlayer.getName()) && o instanceof EntityPlayer && mc.thePlayer.getDistanceToEntity(o) < range) {
            double dist = (double)mc.thePlayer.getDistanceToEntity(o);
            if (dist < closestDist) {
               closestDist = dist;
               player = (EntityPlayer)o;
            }
         }
      }

      return player;
   }
}
