package intent.AquaDev.aqua.modules.movement;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPlayerMove;
import events.listeners.EventRenderNameTags;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.modules.combat.Killaura;
import intent.AquaDev.aqua.modules.visual.Arraylist;
import intent.AquaDev.aqua.modules.visual.Shadow;
import intent.AquaDev.aqua.utils.PlayerUtil;
import intent.AquaDev.aqua.utils.RotationUtil;
import java.awt.Color;
import java.util.Random;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class TargetStrafe extends Module {
   private int direction = -1;

   public TargetStrafe() {
      super("TargetStrafe", Module.Type.Movement, "TargetStrafe", 0, Category.Movement);
      Aqua.setmgr.register(new Setting("Circle", this, true));
      Aqua.setmgr.register(new Setting("Watchdog", this, true));
      Aqua.setmgr.register(new Setting("OnlySpeed", this, false));
      Aqua.setmgr.register(new Setting("StrafeRange", this, 4.0, 0.3, 9.0, false));
      Aqua.setmgr.register(new Setting("LineWidth", this, 2.0, 1.0, 10.0, false));
      Aqua.setmgr.register(new Setting("OnlyJump", this, true));
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
      if (event instanceof EventRenderNameTags) {
         float range = (float)Aqua.setmgr.getSetting("TargetStrafeStrafeRange").getCurrentNumber();
         if (Aqua.setmgr.getSetting("TargetStrafeCircle").isState()) {
            this.drawCircle(mc.timer.renderPartialTicks, (double)range);
            Arraylist.drawGlowArray(() -> this.drawCircle(mc.timer.renderPartialTicks, (double)range), false);
            Shadow.drawGlow(() -> this.drawCircle(mc.timer.renderPartialTicks, (double)range), false);
         }
      }

      if (event instanceof EventPlayerMove) {
         this.doStrafeAtSpeed(EventPlayerMove.INSTANCE, PlayerUtil.getSpeed());
      }

      if (event instanceof EventUpdate) {
         if (mc.thePlayer.isCollidedHorizontally) {
         }

         if (!this.isBlockUnder()) {
         }

         if (mc.gameSettings.keyBindRight.pressed) {
            this.direction = -1;
         } else if (mc.gameSettings.keyBindLeft.pressed) {
            this.direction = 1;
         }
      }
   }

   private void switchDirection() {
      this.direction *= -1;
   }

   public void doStrafeAtSpeed(EventPlayerMove event, double moveSpeed) {
      float range = (float)Aqua.setmgr.getSetting("TargetStrafeStrafeRange").getCurrentNumber();
      boolean strafe = this.canStrafe();
      if (!Aqua.setmgr.getSetting("TargetStrafeOnlySpeed").isState()) {
         if (Killaura.target != null && strafe) {
            float[] rotations = RotationUtil.Intavee(mc.thePlayer, Killaura.target);
            if (Aqua.setmgr.getSetting("TargetStrafeOnlyJump").isState()) {
               if (Aqua.moduleManager.getModuleByName("Fly").isToggled()) {
                  if (mc.thePlayer.getDistanceToEntity(Killaura.target) <= range) {
                     if (Aqua.setmgr.getSetting("TargetStrafeWatchdog").isState()) {
                        if (mc.thePlayer.hurtTime != 0) {
                           this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 0.0);
                        }
                     } else {
                        this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 0.0);
                     }
                  } else if (Aqua.setmgr.getSetting("TargetStrafeWatchdog").isState()) {
                     if (mc.thePlayer.hurtTime != 0) {
                        this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 1.0);
                     }
                  } else {
                     this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 1.0);
                  }
               } else if (mc.gameSettings.keyBindJump.pressed) {
                  if (mc.thePlayer.getDistanceToEntity(Killaura.target) <= range) {
                     if (Aqua.setmgr.getSetting("TargetStrafeWatchdog").isState()) {
                        if (mc.thePlayer.hurtTime != 0) {
                           this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 0.0);
                        }
                     } else {
                        this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 0.0);
                     }
                  } else if (Aqua.setmgr.getSetting("TargetStrafeWatchdog").isState()) {
                     if (mc.thePlayer.hurtTime != 0) {
                        this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 1.0);
                     }
                  } else {
                     this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 1.0);
                  }
               }
            } else if (mc.thePlayer.getDistanceToEntity(Killaura.target) <= range) {
               if (Aqua.setmgr.getSetting("TargetStrafeWatchdog").isState()) {
                  if (mc.thePlayer.hurtTime != 0) {
                     this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 0.0);
                  }
               } else {
                  this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 0.0);
               }
            } else if (Aqua.setmgr.getSetting("TargetStrafeWatchdog").isState()) {
               if (mc.thePlayer.hurtTime != 0) {
                  this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 1.0);
               }
            } else {
               this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 1.0);
            }
         }
      } else if (Aqua.moduleManager.getModuleByName("Speed").isToggled() && Killaura.target != null && strafe) {
         float[] rotations = RotationUtil.Intavee(mc.thePlayer, Killaura.target);
         if (Aqua.setmgr.getSetting("TargetStrafeOnlyJump").isState()) {
            if (Aqua.moduleManager.getModuleByName("Fly").isToggled()) {
               if (mc.thePlayer.getDistanceToEntity(Killaura.target) <= range) {
                  if (Aqua.setmgr.getSetting("TargetStrafeWatchdog").isState()) {
                     if (mc.thePlayer.hurtTime != 0) {
                        this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 0.0);
                     }
                  } else {
                     this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 0.0);
                  }
               } else if (Aqua.setmgr.getSetting("TargetStrafeWatchdog").isState()) {
                  if (mc.thePlayer.hurtTime != 0) {
                     this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 1.0);
                  }
               } else {
                  this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 1.0);
               }
            } else if (mc.gameSettings.keyBindJump.pressed) {
               if (mc.thePlayer.getDistanceToEntity(Killaura.target) <= range) {
                  if (Aqua.setmgr.getSetting("TargetStrafeWatchdog").isState()) {
                     if (mc.thePlayer.hurtTime != 0) {
                        this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 0.0);
                     }
                  } else {
                     this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 0.0);
                  }
               } else if (Aqua.setmgr.getSetting("TargetStrafeWatchdog").isState()) {
                  if (mc.thePlayer.hurtTime != 0) {
                     this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 1.0);
                  }
               } else {
                  this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 1.0);
               }
            }
         } else if (mc.thePlayer.getDistanceToEntity(Killaura.target) <= range) {
            if (Aqua.setmgr.getSetting("TargetStrafeWatchdog").isState()) {
               if (mc.thePlayer.hurtTime != 0) {
                  this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 0.0);
               }
            } else {
               this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 0.0);
            }
         } else if (Aqua.setmgr.getSetting("TargetStrafeWatchdog").isState()) {
            if (mc.thePlayer.hurtTime != 0) {
               this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 1.0);
            }
         } else {
            this.setSpeed1(event, moveSpeed, rotations[0], (double)this.direction, 1.0);
         }
      }
   }

   public boolean canStrafe() {
      return Aqua.moduleManager.getModuleByName("Killaura").isToggled() && Killaura.target != null;
   }

   public boolean isBlockUnder() {
      for(int i = (int)mc.thePlayer.posY; i >= 0; --i) {
         BlockPos position = new BlockPos(mc.thePlayer.posX, (double)i, mc.thePlayer.posZ);
         if (!(mc.theWorld.getBlockState(position).getBlock() instanceof BlockAir)) {
            return true;
         }
      }

      return false;
   }

   public void setSpeed1(EventPlayerMove moveEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe, double pseudoForward) {
      double forward = pseudoForward;
      double strafe = pseudoStrafe;
      float yaw = pseudoYaw;
      if (pseudoForward != 0.0) {
         if (pseudoStrafe > 0.0) {
            yaw = pseudoYaw + (float)(pseudoForward > 0.0 ? -45 : 45);
         } else if (pseudoStrafe < 0.0) {
            yaw = pseudoYaw + (float)(pseudoForward > 0.0 ? 45 : -45);
         }

         strafe = 0.0;
         float strafe1 = (float)MathHelper.getRandomDoubleInRange(new Random(), 0.6, 1.0);
         if (pseudoForward > 0.0) {
            forward = Aqua.setmgr.getSetting("TargetStrafeWatchdog").isState() ? (double)strafe1 : 1.0;
         } else if (pseudoForward < 0.0) {
            forward = Aqua.setmgr.getSetting("TargetStrafeWatchdog").isState() ? (double)(-strafe1) : 1.0;
         }
      }

      if (strafe > 0.0) {
         strafe = 1.0;
      } else if (strafe < 0.0) {
         strafe = -1.0;
      }

      double mx = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
      double mz = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
      moveEvent.setX(forward * moveSpeed * mx + strafe * moveSpeed * mz);
      moveEvent.setZ(forward * moveSpeed * mz - strafe * moveSpeed * mx);
   }

   private void drawCircle(float partialTicks, double rad) {
      if (Killaura.target != null) {
         GL11.glPushMatrix();
         GL11.glDisable(3553);
         GL11.glDisable(2929);
         GL11.glDepthMask(false);
         float width = (float)Aqua.setmgr.getSetting("TargetStrafeLineWidth").getCurrentNumber();
         GL11.glLineWidth(width);
         GL11.glBegin(3);
         double x = Killaura.target.lastTickPosX
            + (Killaura.target.posX - Killaura.target.lastTickPosX) * (double)partialTicks
            - mc.getRenderManager().viewerPosX;
         double y = Killaura.target.lastTickPosY
            + (Killaura.target.posY - Killaura.target.lastTickPosY) * (double)partialTicks
            - mc.getRenderManager().viewerPosY;
         double z = Killaura.target.lastTickPosZ
            + (Killaura.target.posZ - Killaura.target.lastTickPosZ) * (double)partialTicks
            - mc.getRenderManager().viewerPosZ;
         int rgb = Aqua.setmgr.getSetting("HUDColor").getColor();
         float r = 0.003921569F * (float)new Color(rgb).getRed();
         float g = 0.003921569F * (float)new Color(rgb).getGreen();
         float b = 0.003921569F * (float)new Color(rgb).getBlue();
         double pix2 = Math.PI * 2;

         for(int i = 0; i <= 90; ++i) {
            GL11.glColor3f(r, g, b);
            GL11.glVertex3d(x + rad * Math.cos((double)i * pix2 / 45.0), y, z + rad * Math.sin((double)i * pix2 / 45.0));
         }

         GL11.glEnd();
         GL11.glDepthMask(true);
         GL11.glEnable(2929);
         GL11.glEnable(3553);
         GL11.glPopMatrix();
      }
   }
}
