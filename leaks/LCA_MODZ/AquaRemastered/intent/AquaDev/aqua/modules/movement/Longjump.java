package intent.AquaDev.aqua.modules.movement;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventClick;
import events.listeners.EventPreMotion;
import events.listeners.EventTick;
import events.listeners.EventTimerDisabler;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.PlayerUtil;
import intent.AquaDev.aqua.utils.Test;
import intent.AquaDev.aqua.utils.TimeUtil;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;

public class Longjump extends Module {
   public static double Packets;
   public static boolean jump;
   public static boolean startUP;
   public double posY;
   public int stage;
   public int jumps;
   public boolean dmg;
   public int groundTicks;
   TimeUtil timer = new TimeUtil();
   TimeUtil timeUtil = new TimeUtil();
   public boolean hittet = false;
   Test test = new Test();

   public Longjump() {
      super("Longjump", Module.Type.Movement, "Longjump", 0, Category.Movement);
      Aqua.setmgr.register(new Setting("NCPBoost", this, 3.0, 0.3, 9.0, false));
      Aqua.setmgr.register(new Setting("Mode", this, "Watchdog", new String[]{"Watchdog", "WatchdogBow", "Gamster", "OldNCP", "Hycraft", "Cubecraft"}));
   }

   @Override
   public void onEnable() {
      this.dmg = false;
      if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("OldNCP")) {
         mc.thePlayer.jump();
      }

      if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("Hycraft")) {
         mc.getNetHandler()
            .addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
         mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
         mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
         mc.gameSettings.keyBindForward.pressed = false;
      }

      if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("Cubecraft")) {
         mc.thePlayer.motionY = 1.5;
      }

      this.hittet = false;
      PlayerUtil.setSpeed(0.0);
      jump = false;
      startUP = true;
      mc.gameSettings.keyBindUseItem.pressed = Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("WatchdogBow");
      Aqua.moduleManager.getModuleByName("Killaura").setState(false);
      if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("Gamster")) {
         if (mc.thePlayer.onGround) {
            mc.thePlayer.jump();
         }

         mc.timer.timerSpeed = 0.09F;
      }

      if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("Watchdog")) {
         PlayerUtil.setSpeed(0.0);
         mc.gameSettings.keyBindForward.pressed = false;
         mc.thePlayer.motionX = 0.0;
         mc.thePlayer.motionZ = 0.0;
         this.dmg = false;
         this.posY = mc.thePlayer.posY;
         jump = false;
         this.stage = 0;
         this.jumps = 0;
         this.groundTicks = 0;
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.dmg = false;
      if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("OldNCP")) {
         PlayerUtil.setSpeed(0.0);
      }

      this.hittet = false;
      this.timeUtil.reset();
      jump = false;
      mc.thePlayer.jumpMovementFactor = 0.0F;
      mc.thePlayer.capabilities.isFlying = false;
      mc.timer.timerSpeed = 1.0F;
      mc.thePlayer.capabilities.allowFlying = false;
      startUP = false;
      Aqua.moduleManager.getModuleByName("Killaura").setState(true);
      if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("Gamster")) {
      }

      if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("Watchdog")) {
         mc.gameSettings.keyBindJump.pressed = false;
         this.dmg = false;
         this.jumps = 0;
         this.stage = 0;
         this.groundTicks = 0;
         this.timer.reset();
         jump = false;
      }

      mc.timer.timerSpeed = 1.0F;
      super.onDisable();
   }

   @Override
   public void onEvent(Event e) {
      mc.thePlayer.cameraYaw = 0.045F;
      if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("WatchdogBow") && mc.thePlayer.ticksExisted % 7 == 0) {
         mc.gameSettings.keyBindUseItem.pressed = false;
      }

      if (e instanceof EventTick && Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("Gamster")) {
         if (mc.thePlayer.ticksExisted % 5 == 0) {
            mc.gameSettings.keyBindUseItem.pressed = false;
            mc.timer.timerSpeed = 1.0F;
         } else {
            mc.timer.timerSpeed = 0.1F;
            mc.gameSettings.keyBindUseItem.pressed = true;
         }
      }

      if (e instanceof EventUpdate) {
         if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("Cubecraft")) {
            Fly.sendPacketUnlogged(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, new ItemStack(Items.water_bucket), 0.0F, 0.5F, 0.0F));
            Fly.sendPacketUnlogged(
               new C08PacketPlayerBlockPlacement(
                  new BlockPos(Fly.getX(), Fly.getY() - 1.5, Fly.getZ()),
                  1,
                  new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))),
                  0.0F,
                  0.94F,
                  0.0F
               )
            );
            double speed1 = (double)((float)Aqua.setmgr.getSetting("LongjumpNCPBoost").getCurrentNumber());
            PlayerUtil.setSpeed(speed1);
         }

         if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("Hycraft")) {
            if (mc.thePlayer.hurtTime != 0) {
               PlayerUtil.setSpeed(0.3);
               mc.timer.timerSpeed = 1.0F;
               mc.thePlayer.motionY = 0.42F;
               this.dmg = true;
            } else {
               mc.timer.timerSpeed = 1.0F;
            }

            if (this.dmg) {
               mc.gameSettings.keyBindForward.pressed = true;
               mc.thePlayer.motionY += 0.03F;
            }
         }

         if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("OldNCP")) {
            Fly.sendPacketUnlogged(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, new ItemStack(Items.water_bucket), 0.0F, 0.5F, 0.0F));
            Fly.sendPacketUnlogged(
               new C08PacketPlayerBlockPlacement(
                  new BlockPos(Fly.getX(), Fly.getY() - 1.5, Fly.getZ()),
                  1,
                  new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))),
                  0.0F,
                  0.94F,
                  0.0F
               )
            );
            mc.timer.timerSpeed = 0.8F;
            double speed1 = (double)((float)Aqua.setmgr.getSetting("LongjumpNCPBoost").getCurrentNumber());
            PlayerUtil.setSpeed(speed1);
            if (mc.thePlayer.fallDistance > 0.0F) {
               mc.thePlayer.motionY += 0.04F;
            }
         }

         if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("WatchdogBow")) {
            if (mc.thePlayer.hurtTime != 0) {
               jump = true;
            }

            if (!jump) {
               mc.gameSettings.keyBindForward.pressed = false;
            }

            if (jump) {
               if (!mc.thePlayer.onGround) {
               }

               mc.gameSettings.keyBindForward.pressed = true;
            }

            if (mc.thePlayer.hurtTime == 0) {
               mc.timer.timerSpeed = 0.1F;
            }

            if (mc.thePlayer.hurtTime != 0) {
               if (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null) {
                  PlayerUtil.setSpeed(0.66);
               } else {
                  PlayerUtil.setSpeed(0.52);
               }
            }

            mc.thePlayer.jumpMovementFactor = 0.025F;
            if ((
                  mc.gameSettings.keyBindForward.pressed
                     || mc.gameSettings.keyBindLeft.pressed
                     || mc.gameSettings.keyBindRight.pressed
                     || mc.gameSettings.keyBindBack.pressed
               )
               && mc.thePlayer.onGround) {
               mc.thePlayer.motionY = 0.42F;
               mc.timer.timerSpeed = 1.0F;
               mc.thePlayer.setSprinting(true);
            } else {
               mc.thePlayer.setSprinting(true);
               mc.timer.timerSpeed = 1.0F;
               if (mc.thePlayer.hurtTime != 0) {
                  mc.thePlayer.motionY += 0.028F;
                  if ((double)mc.thePlayer.fallDistance > 0.1 && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null) {
                     mc.thePlayer.motionY = 0.42F;
                  }

                  if (mc.thePlayer.fallDistance > 0.0F) {
                     mc.thePlayer.motionY += 0.018;
                  }
               }
            }
         }

         if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("Gamster") && mc.thePlayer.hurtTime != 0) {
         }
      }

      if (e instanceof EventPreMotion) {
         if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("Gamster")) {
            if (mc.thePlayer.hurtTime != 0) {
               this.hittet = true;
               ((EventPreMotion)e).setPitch(-40.0F);
            } else {
               if (mc.thePlayer.onGround) {
                  mc.thePlayer.jump();
               }

               if (!this.hittet) {
                  ((EventPreMotion)e).setPitch(-85.0F);
               } else {
                  ((EventPreMotion)e).setPitch(-30.0F);
               }
            }
         }

         if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("WatchdogBow")) {
            ((EventPreMotion)e).setPitch(-90.0F);
         }

         if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("Watchdog")) {
            if (this.jumps < 3) {
               mc.thePlayer.posY = this.posY;
            } else {
               mc.timer.timerSpeed = 1.0F;
            }
         }
      }

      if (e instanceof EventTimerDisabler && Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("Watchdog")) {
         Packet packet = EventTimerDisabler.getPacket();
         if (packet instanceof C03PacketPlayer) {
            C03PacketPlayer packetPlayer = (C03PacketPlayer)packet;
            if (this.jumps < 3) {
               packetPlayer.onGround = false;
            }
         }
      }

      if (e instanceof EventClick) {
         if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("Watchdog")) {
            if (mc.thePlayer.onGround && this.jumps < 3) {
               mc.thePlayer.jump();
               ++this.jumps;
               mc.gameSettings.keyBindForward.pressed = false;
            }

            if (mc.thePlayer.hurtTime != 0) {
               jump = true;
               if (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null) {
                  PlayerUtil.setSpeed(0.525);
               } else {
                  PlayerUtil.setSpeed(0.4);
               }

               mc.timer.timerSpeed = 1.0F;
            } else {
               mc.gameSettings.keyBindForward.pressed = false;
            }

            if (jump) {
               mc.gameSettings.keyBindForward.pressed = true;
               mc.gameSettings.keyBindJump.pressed = true;
               if (!mc.thePlayer.onGround) {
                  mc.thePlayer.motionY += 0.025;
               }
            }
         }

         if (Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("Gamster")) {
         }
      }
   }
}
