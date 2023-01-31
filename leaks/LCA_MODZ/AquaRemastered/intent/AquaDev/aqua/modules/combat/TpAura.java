package intent.AquaDev.aqua.modules.combat;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPreMotion;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.FriendSystem;
import intent.AquaDev.aqua.utils.PathFinder;
import intent.AquaDev.aqua.utils.RotationUtil;
import intent.AquaDev.aqua.utils.TimeUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;

public class TpAura extends Module {
   public double delay;
   Entity target = null;
   TimeUtil time = new TimeUtil();

   public TpAura() {
      super("TpAura", Module.Type.Combat, "TpAura", 0, Category.Combat);
      Aqua.setmgr.register(new Setting("TpDelay", this, 50.0, 0.0, 1000.0, false));
   }

   public static List<Vec3> calculatePath(Vec3 startPos, Vec3 endPos) {
      PathFinder pathfinder = new PathFinder(startPos, endPos);
      pathfinder.calculatePath(2000);
      int i = 0;
      Vec3 lastLoc = null;
      Vec3 lastDashLoc = null;
      List<Vec3> path = new ArrayList<>();
      List<Vec3> pathFinderPath = pathfinder.getPath();

      for(Vec3 pathElm : pathFinderPath) {
         if (i != 0 && i != pathFinderPath.size() - 1) {
            boolean canContinue = true;
            if (pathElm.squareDistanceTo(lastDashLoc) > 10.0) {
               canContinue = false;
            } else {
               double smallX = Math.min(lastDashLoc.xCoord, pathElm.xCoord);
               double smallY = Math.min(lastDashLoc.yCoord, pathElm.yCoord);
               double smallZ = Math.min(lastDashLoc.zCoord, pathElm.zCoord);
               double bigX = Math.max(lastDashLoc.xCoord, pathElm.xCoord);
               double bigY = Math.max(lastDashLoc.yCoord, pathElm.yCoord);
               double bigZ = Math.max(lastDashLoc.zCoord, pathElm.zCoord);

               label50:
               for(int x = (int)smallX; (double)x <= bigX; ++x) {
                  for(int y = (int)smallY; (double)y <= bigY; ++y) {
                     for(int z = (int)smallZ; (double)z <= bigZ; ++z) {
                        if (!PathFinder.checkPositionValidity(x, y, z, false)) {
                           canContinue = false;
                           break label50;
                        }
                     }
                  }
               }
            }

            if (!canContinue) {
               path.add(lastLoc.addVector(0.5, 0.0, 0.5));
               lastDashLoc = lastLoc;
            }
         } else {
            if (lastLoc != null) {
               path.add(lastLoc.addVector(0.5, 0.0, 0.5));
            }

            path.add(pathElm.addVector(0.5, 0.0, 0.5));
            lastDashLoc = pathElm;
         }

         lastLoc = pathElm;
         ++i;
      }

      return path;
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
      if (e instanceof EventPreMotion) {
         float[] rota = RotationUtil.Intavee(mc.thePlayer, (EntityLivingBase)this.target);
         ((EventPreMotion)e).setPitch(RotationUtil.pitch);
         ((EventPreMotion)e).setYaw(RotationUtil.yaw);
         RotationUtil.setYaw(rota[0], 180.0F);
         RotationUtil.setPitch(rota[1], 180.0F);
      }

      if (e instanceof EventUpdate) {
         float delay = (float)Aqua.setmgr.getSetting("TpAuraTpDelay").getCurrentNumber();
         if (this.time.hasReached((long)delay)) {
            if (!mc.thePlayer.isMoving()) {
               this.attack(this.modes());
            }

            this.time.reset();
         }
      }
   }

   public Entity modes() {
      for(EntityPlayer entity : mc.theWorld.playerEntities) {
         if (entity != mc.thePlayer && (this.target == null || entity.getDistanceToEntity(mc.thePlayer) < this.target.getDistanceToEntity(mc.thePlayer))) {
            this.target = entity;
         }
      }

      return this.target;
   }

   public void attack(Entity entity) {
      if (entity != null) {
         List<Vec3> path = calculatePath(mc.thePlayer.getPositionVector(), entity.getPositionVector());

         for(Vec3 pos : path) {
            if (FriendSystem.isFriend(entity.getName())) {
               return;
            }

            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pos.xCoord, pos.yCoord, pos.zCoord, false));
         }

         if (mc.getCurrentServerData().serverIP.equalsIgnoreCase("cubecraft.net")) {
            mc.playerController.attackEntity(mc.thePlayer, this.target);
            mc.thePlayer.swingItem();
         } else {
            mc.thePlayer.swingItem();
            mc.playerController.attackEntity(mc.thePlayer, entity);
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
         }

         Collections.reverse(path);

         for(Vec3 pos : path) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pos.xCoord, pos.yCoord, pos.zCoord, false));
         }
      }
   }
}
