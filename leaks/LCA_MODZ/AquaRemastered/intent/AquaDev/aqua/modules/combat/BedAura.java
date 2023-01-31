package intent.AquaDev.aqua.modules.combat;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPreMotion;
import events.listeners.EventSilentMove;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.FriendSystem;
import intent.AquaDev.aqua.utils.RotationUtil;
import intent.AquaDev.aqua.utils.TimeUtil;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class BedAura extends Module {
   public TimeUtil timeUtil = new TimeUtil();
   public static BlockPos pos;
   public static boolean rotating = false;
   public static EntityPlayer target = null;
   float[] rota = null;

   public BedAura() {
      super("BedAura", Module.Type.Combat, "BedAura", 0, Category.Combat);
      Aqua.setmgr.register(new Setting("Range", this, 6.0, 3.0, 6.0, false));
      Aqua.setmgr.register(new Setting("minCPS", this, 17.0, 1.0, 20.0, false));
      Aqua.setmgr.register(new Setting("maxCPS", this, 19.0, 1.0, 20.0, false));
      Aqua.setmgr.register(new Setting("CorrectMovement", this, false));
      Aqua.setmgr.register(new Setting("MoveFixMode", this, "Silent", new String[]{"Silent", "Normal"}));
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      rotating = false;
      super.onDisable();
   }

   @Override
   public void onEvent(Event event) {
      if (event instanceof EventSilentMove
         && Aqua.setmgr.getSetting("BedAuraMoveFixMode").getCurrentMode().equalsIgnoreCase("Silent")
         && rotating
         && Aqua.setmgr.getSetting("BedAuraCorrectMovement").isState()) {
         ((EventSilentMove)event).setSilent(true);
      }

      if (event instanceof EventPreMotion && target == null) {
         for(int y = 3; (float)y >= -3.2F; --y) {
            for(int x = -3; (float)x <= 3.2F; ++x) {
               for(int z = -3; (float)z <= 3.2F; ++z) {
                  rotating = false;
               }
            }
         }

         for(int y = 3; y >= -3; --y) {
            for(int x = -3; (float)x <= 3.0F; ++x) {
               for(int z = -3; (float)z <= 3.0F; ++z) {
                  int posX = (int)(mc.thePlayer.posX - 0.5 + (double)x);
                  int posZ = (int)(mc.thePlayer.posZ - 0.5 + (double)z);
                  int posY = (int)(mc.thePlayer.posY - 0.5 + (double)y);
                  pos = new BlockPos(posX, posY, posZ);
                  Block block = mc.theWorld.getBlockState(pos).getBlock();
                  if (block instanceof BlockBed) {
                     rotating = true;
                     this.rota = RotationUtil.lookAtPosBed((double)pos.getX() + 0.5, (double)pos.getY() - 0.5, (double)pos.getZ() + 0.5);
                  }
               }
            }
         }

         if (rotating) {
            ((EventPreMotion)event).setPitch(RotationUtil.pitch);
            ((EventPreMotion)event).setYaw(RotationUtil.yaw);
            RotationUtil.setYaw(this.rota[0], 180.0F);
            RotationUtil.setPitch(this.rota[1], 8.0F);
         }
      }

      if (event instanceof EventUpdate) {
         target = this.searchTargets();
         if (target == null && !mc.thePlayer.isSwingInProgress) {
            mc.gameSettings.keyBindAttack.pressed = false;
         }

         if (target != null) {
            Aqua.moduleManager.getModuleByName("Killaura").setState(true);
         }

         if (target == null) {
            float minCPS = (float)Aqua.setmgr.getSetting("BedAuraminCPS").getCurrentNumber();
            float maxCPS = (float)Aqua.setmgr.getSetting("BedAuramaxCPS").getCurrentNumber();
            float CPS = (float)MathHelper.getRandomDoubleInRange(new Random(), (double)minCPS, (double)maxCPS);
            int radius = 3;

            for(int y = -3; y <= 3; ++y) {
               for(int x = -3; x <= 3; ++x) {
                  for(int z = -3; z <= 3; ++z) {
                     BlockPos blockPos = new BlockPos(mc.thePlayer.posX + (double)x, mc.thePlayer.posY + (double)y, mc.thePlayer.posZ + (double)z);
                     Block block = mc.theWorld.getBlockState(blockPos).getBlock();
                     if (block instanceof BlockBed) {
                        if (this.timeUtil.hasReached((long)(1000.0F / CPS))) {
                           if (rotating) {
                              mc.thePlayer.setSprinting(false);
                              mc.gameSettings.keyBindAttack.pressed = true;
                           } else {
                              mc.gameSettings.keyBindAttack.pressed = false;
                           }

                           mc.thePlayer.swingItem();
                           mc.clickMouse();
                           this.timeUtil.reset();
                        }

                        Aqua.moduleManager.getModuleByName("Killaura").setState(false);
                     }
                  }
               }
            }
         }
      }
   }

   public EntityPlayer searchTargets() {
      float range = (float)Aqua.setmgr.getSetting("KillauraRange").getCurrentNumber();
      EntityPlayer player = null;
      double closestDist = 100000.0;

      for(Entity o : mc.theWorld.loadedEntityList) {
         if (!o.getName().equals(mc.thePlayer.getName())
            && o instanceof EntityPlayer
            && !FriendSystem.isFriend(o.getName())
            && !Antibot.bots.contains(o)
            && !mc.session.getUsername().equalsIgnoreCase("Administradora")
            && mc.thePlayer.getDistanceToEntity(o) < range) {
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
