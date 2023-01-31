package intent.AquaDev.aqua.modules.movement;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPacket;
import events.listeners.EventPlayerMove;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.modules.combat.Killaura;
import intent.AquaDev.aqua.notifications.Notification;
import intent.AquaDev.aqua.notifications.NotificationManager;
import intent.AquaDev.aqua.utils.PlayerUtil;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoubleStoneSlab;
import net.minecraft.block.BlockDoubleStoneSlabNew;
import net.minecraft.block.BlockDoubleWoodSlab;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class Speed extends Module {
   public Speed() {
      super("Speed", Module.Type.Movement, "Speed", 0, Category.Movement);
      Aqua.setmgr.register(new Setting("MotionReset", this, true));
      Aqua.setmgr.register(new Setting("AutoDisable", this, true));
      Aqua.setmgr.register(new Setting("WatchdogBoost", this, false));
      Aqua.setmgr.register(new Setting("Speed", this, 1.0, 0.3, 9.0, false));
      Aqua.setmgr.register(new Setting("TimerBoost", this, 1.3, 1.0, 3.0, false));
      Aqua.setmgr.register(new Setting("YMotion", this, 0.42, 0.0, 0.9, false));
      Aqua.setmgr.register(new Setting("SentinelSpeed", this, 3.0, 0.3, 9.0, false));
      Aqua.setmgr
         .register(
            new Setting(
               "Mode",
               this,
               "Watchdog",
               new String[]{"Watchdog", "Watchdog2", "WatchdogNew", "WatchdogSave", "Vanilla", "Strafe", "AAC3", "Intave14", "Cubecraft", "CubecraftBhob"}
            )
         );
   }

   @Override
   public void onEnable() {
      if (Aqua.setmgr.getSetting("SpeedWatchdogBoost").isState()) {
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      mc.timer.timerSpeed = 1.0F;
      mc.gameSettings.keyBindJump.pressed = false;
      PlayerUtil.setSpeed(0.0);
      super.onDisable();
   }

   @Override
   public void onEvent(Event event) {
      if (event instanceof EventPacket) {
         Packet packet = EventPacket.getPacket();
         if (packet instanceof S08PacketPlayerPosLook && Aqua.setmgr.getSetting("SpeedAutoDisable").isState()) {
            NotificationManager.addNotificationToQueue(new Notification("Speed", "§cDisabled to prevent flags", 1000L, Notification.NotificationType.INFO));
            Aqua.moduleManager.getModuleByName("Speed").setState(false);
         }
      }

      if (event instanceof EventUpdate) {
         if (Aqua.setmgr.getSetting("SpeedMode").getCurrentMode().equalsIgnoreCase("Cubecraft")) {
            if (mc.thePlayer.isMoving() && mc.thePlayer.onGround) {
               float blockStep = 1.5F;
               double[] dir = this.getDirection1((double)((float)Aqua.setmgr.getSetting("SpeedSentinelSpeed").getCurrentNumber()));
               if (mc.thePlayer.ticksExisted % 3 == 0) {
                  mc.thePlayer.setPosition(mc.thePlayer.posX + dir[0], mc.thePlayer.posY, mc.thePlayer.posZ + dir[1]);
               }
            }

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
         }

         if (Aqua.setmgr.getSetting("SpeedMode").getCurrentMode().equalsIgnoreCase("CubecraftBhob")) {
            PlayerUtil.setSpeed(PlayerUtil.getSpeed());
            if (mc.thePlayer.onGround) {
               mc.thePlayer.jump();
               PlayerUtil.setSpeed(0.0);
               mc.timer.timerSpeed = 1.5F;
               double[] dir = this.getDirection1((double)((float)Aqua.setmgr.getSetting("SpeedSentinelSpeed").getCurrentNumber()));
               mc.thePlayer.setPosition(mc.thePlayer.posX + dir[0], mc.thePlayer.posY, mc.thePlayer.posZ + dir[1]);
            }

            if (mc.thePlayer.isMoving()) {
               mc.timer.timerSpeed = 2.0F;
               if (mc.thePlayer.fallDistance > 0.0F) {
                  float blockStep = 1.5F;
                  double[] var19 = this.getDirection1((double)((float)Aqua.setmgr.getSetting("SpeedSentinelSpeed").getCurrentNumber()));
               }
            }

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
         }

         if (Aqua.setmgr.getSetting("SpeedMode").getCurrentMode().equalsIgnoreCase("Vanilla")) {
            if (mc.thePlayer.onGround) {
               mc.thePlayer.motionY = (double)((float)Aqua.setmgr.getSetting("SpeedYMotion").getCurrentNumber());
            }

            PlayerUtil.setSpeed((double)((float)Aqua.setmgr.getSetting("SpeedSpeed").getCurrentNumber()));
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
         }

         if (Aqua.setmgr.getSetting("SpeedMode").getCurrentMode().equalsIgnoreCase("Intave14")) {
            boolean flag = this.getBlockUnderPlayer(0.1F) instanceof BlockStairs
               || this.getBlockUnderPlayer(0.1F) instanceof BlockSlab
                  && !(this.getBlockUnderPlayer(0.1F) instanceof BlockDoubleWoodSlab)
                  && (!(this.getBlockUnderPlayer(0.1F) instanceof BlockDoubleStoneSlab) || this.getBlockUnderPlayer(0.1F) instanceof BlockDoubleStoneSlabNew);
            if (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null
               && Aqua.moduleManager.getModuleByName("Speed").isToggled()
               && mc.thePlayer.onGround) {
               float t = (float)MathHelper.getRandomDoubleInRange(new Random(), 0.186, 0.1861);
               if (!mc.gameSettings.keyBindJump.pressed) {
                  mc.thePlayer.jump();
               }

               if (!flag) {
               }
            }

            if (mc.thePlayer.onGround
               && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) == null
               && !mc.gameSettings.keyBindJump.pressed
               && mc.thePlayer.isMoving()) {
               mc.thePlayer.jump();
            }

            if (!mc.thePlayer.onGround && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) == null) {
               float strafe1 = (float)MathHelper.getRandomDoubleInRange(new Random(), 0.00283F, 0.002832F);
               if (Aqua.setmgr.getSetting("SpeedWatchdogBoost").isState() && !flag) {
                  mc.thePlayer.motionY -= (double)strafe1;
               }
            }

            if (!mc.thePlayer.onGround
               && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null
               && Aqua.setmgr.getSetting("SpeedWatchdogBoost").isState()) {
               float strafe1 = (float)MathHelper.getRandomDoubleInRange(new Random(), 0.0019F, 0.002F);
               if (!flag) {
                  mc.thePlayer.motionY -= (double)strafe1;
               }
            }
         }

         if (Aqua.setmgr.getSetting("SpeedMode").getCurrentMode().equalsIgnoreCase("AAC3")) {
            boolean boost = Math.abs(mc.thePlayer.rotationYawHead - mc.thePlayer.rotationYaw) < 90.0F;
            if (mc.thePlayer.isMoving() && mc.thePlayer.hurtTime < 5) {
               if (mc.thePlayer.onGround) {
                  mc.timer.timerSpeed = 1.0F;
                  if (!Aqua.moduleManager.getModuleByName("Scaffold").isToggled()) {
                     mc.thePlayer.jump();
                  } else {
                     mc.thePlayer.motionY = 0.4;
                  }

                  float f = getDirection();
                  if (!Aqua.moduleManager.getModuleByName("Scaffold").isToggled()) {
                  }
               } else {
                  mc.timer.timerSpeed = 1.0F;
                  mc.thePlayer.speedInAir = 0.021F;
                  double currentSpeed = Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
                  double speed1 = 1.0074;
                  double direction = (double)getDirection();
                  mc.thePlayer.motionX = -Math.sin(direction) * speed1 * currentSpeed;
                  mc.thePlayer.motionZ = Math.cos(direction) * speed1 * currentSpeed;
               }
            }
         }

         if (Aqua.setmgr.getSetting("SpeedMode").getCurrentMode().equalsIgnoreCase("Strafe")) {
            PlayerUtil.setSpeed((double)((float)Aqua.setmgr.getSetting("SpeedSentinelSpeed").getCurrentNumber()));
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
            if (mc.thePlayer.onGround) {
               mc.thePlayer.motionY += 0.42F;
            } else {
               mc.thePlayer.motionY -= 0.03F;
            }
         }

         if (Aqua.setmgr.getSetting("SpeedMode").getCurrentMode().equalsIgnoreCase("WatchdogNew")) {
            if (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) == null) {
               if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.pressed) {
                  mc.thePlayer.jump();
               } else {
                  PlayerUtil.setSpeed(PlayerUtil.getSpeed());
               }
            } else {
               float boost = (float)MathHelper.getRandomDoubleInRange(new Random(), 0.186, 0.18601);
               if (mc.thePlayer.onGround) {
                  if (!mc.gameSettings.keyBindJump.pressed) {
                     mc.thePlayer.jump();
                  }

                  boolean flag = this.getBlockUnderPlayer(0.1F) instanceof BlockStairs
                     || this.getBlockUnderPlayer(0.1F) instanceof BlockSlab
                        && !(this.getBlockUnderPlayer(0.1F) instanceof BlockDoubleWoodSlab)
                        && (
                           !(this.getBlockUnderPlayer(0.1F) instanceof BlockDoubleStoneSlab)
                              || this.getBlockUnderPlayer(0.1F) instanceof BlockDoubleStoneSlabNew
                        );
                  if (!flag) {
                     PlayerUtil.setSpeed(PlayerUtil.getSpeed() + (double)boost);
                  }
               } else {
                  PlayerUtil.setSpeed(PlayerUtil.getSpeed());
               }
            }
         }

         if (Aqua.setmgr.getSetting("SpeedMode").getCurrentMode().equalsIgnoreCase("WatchdogSave") && mc.thePlayer.isMoving()) {
            if (mc.thePlayer.hurtTime != 0) {
               float speed = (float)Aqua.setmgr.getSetting("SpeedTimerBoost").getCurrentNumber();
               mc.timer.timerSpeed = speed;
               if ((mc.thePlayer.isBurning() || Killaura.target == null || !Aqua.setmgr.getSetting("SpeedWatchdogBoost").isState())
                  && (mc.thePlayer.isInLava() || Killaura.target == null || !Aqua.setmgr.getSetting("SpeedWatchdogBoost").isState())) {
                  PlayerUtil.setSpeed(PlayerUtil.getSpeed());
               } else {
                  PlayerUtil.setSpeed(PlayerUtil.getSpeed() + 0.007);
               }
            } else {
               mc.timer.timerSpeed = 1.0F;
            }

            if (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null) {
               float boost = (float)MathHelper.getRandomDoubleInRange(new Random(), 0.186, 0.18601);
               if (mc.thePlayer.onGround) {
                  if (!mc.gameSettings.keyBindJump.pressed) {
                     mc.thePlayer.jump();
                  }

                  boolean flag = this.getBlockUnderPlayer(0.1F) instanceof BlockStairs
                     || this.getBlockUnderPlayer(0.1F) instanceof BlockSlab
                        && !(this.getBlockUnderPlayer(0.1F) instanceof BlockDoubleWoodSlab)
                        && (
                           !(this.getBlockUnderPlayer(0.1F) instanceof BlockDoubleStoneSlab)
                              || this.getBlockUnderPlayer(0.1F) instanceof BlockDoubleStoneSlabNew
                        );
                  if (!flag) {
                     PlayerUtil.setSpeed(PlayerUtil.getSpeed() + (double)boost);
                  }
               }
            } else if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.pressed) {
               mc.thePlayer.jump();
               PlayerUtil.setSpeed(PlayerUtil.getSpeed());
            }
         }

         if (Aqua.setmgr.getSetting("SpeedMode").getCurrentMode().equalsIgnoreCase("Watchdog2")) {
            if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.pressed && mc.thePlayer.isMoving()) {
               mc.thePlayer.jump();
               if (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null) {
                  PlayerUtil.setSpeed(PlayerUtil.getSpeed() + 0.105);
               } else {
                  PlayerUtil.setSpeed(PlayerUtil.getSpeed() + 0.06);
               }

               mc.timer.timerSpeed = 1.0F;
               mc.thePlayer.jumpMovementFactor = 0.04F;
            }

            float strafe1 = (float)MathHelper.getRandomDoubleInRange(new Random(), 0.0023F, 0.00234F);
            if (Aqua.setmgr.getSetting("SpeedWatchdogBoost").isState() && mc.thePlayer.fallDistance > 0.0F) {
               mc.thePlayer.motionY -= (double)strafe1;
            }
         }

         if (Aqua.setmgr.getSetting("SpeedMode").getCurrentMode().equalsIgnoreCase("Watchdog")) {
            if (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null && Aqua.moduleManager.getModuleByName("Speed").isToggled()) {
               if (!mc.thePlayer.onGround) {
                  if (mc.thePlayer.ticksExisted % 2 == 0) {
                     mc.timer.timerSpeed = 0.95F;
                  } else {
                     mc.timer.timerSpeed = 1.23F;
                  }
               } else {
                  mc.timer.timerSpeed = 1.0F;
               }
            }

            boolean flag = this.getBlockUnderPlayer(0.1F) instanceof BlockStairs
               || this.getBlockUnderPlayer(0.1F) instanceof BlockSlab
                  && !(this.getBlockUnderPlayer(0.1F) instanceof BlockDoubleWoodSlab)
                  && (!(this.getBlockUnderPlayer(0.1F) instanceof BlockDoubleStoneSlab) || this.getBlockUnderPlayer(0.1F) instanceof BlockDoubleStoneSlabNew);
            if (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null && Aqua.moduleManager.getModuleByName("Speed").isToggled()) {
               if (mc.thePlayer.onGround) {
                  float t = (float)MathHelper.getRandomDoubleInRange(new Random(), 0.185, 0.185);
                  if (!mc.gameSettings.keyBindJump.pressed) {
                     mc.thePlayer.jump();
                  }

                  if (!flag) {
                     PlayerUtil.setSpeed(PlayerUtil.getSpeed() + (double)t);
                  }
               } else {
                  PlayerUtil.setSpeed(PlayerUtil.getSpeed());
               }
            }

            if (mc.thePlayer.onGround
               && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) == null
               && !mc.gameSettings.keyBindJump.pressed
               && mc.thePlayer.isMoving()) {
               mc.thePlayer.jump();
            }

            if (!mc.thePlayer.onGround && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) == null) {
               float strafe1 = (float)MathHelper.getRandomDoubleInRange(new Random(), 0.00283F, 0.00284F);
               if (Aqua.setmgr.getSetting("SpeedWatchdogBoost").isState() && !flag) {
                  mc.thePlayer.motionY -= (double)strafe1;
               }
            }

            if (!mc.thePlayer.onGround
               && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null
               && Aqua.setmgr.getSetting("SpeedWatchdogBoost").isState()) {
               float strafe1 = (float)MathHelper.getRandomDoubleInRange(new Random(), 0.0019F, 0.002F);
               if (!flag) {
                  mc.thePlayer.motionY -= (double)strafe1;
               }
            }

            PlayerUtil.setSpeed(PlayerUtil.getSpeed());
         }
      }
   }

   public static void setSpeed1(EventPlayerMove moveEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe, double pseudoForward) {
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
         if (pseudoForward > 0.0) {
            forward = 1.0;
         } else if (pseudoForward < 0.0) {
            forward = -1.0;
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

   Block getBlockUnderPlayer(float offsetY) {
      return this.getBlockUnderPlayer(mc.thePlayer, offsetY);
   }

   Block getBlockUnderPlayer(EntityPlayer player, float offsetY) {
      return this.getWorld().getBlockState(new BlockPos(player.posX, player.posY - (double)offsetY, player.posZ)).getBlock();
   }

   WorldClient getWorld() {
      return mc.theWorld;
   }

   public static float getDirection() {
      float var1 = mc.thePlayer.rotationYaw;
      if (mc.thePlayer.moveForward < 0.0F) {
         var1 += 180.0F;
      }

      float forward = 1.0F;
      if (mc.thePlayer.moveForward < 0.0F) {
         forward = -0.5F;
      } else if (mc.thePlayer.moveForward > 0.0F) {
         forward = 0.5F;
      }

      if (mc.thePlayer.moveStrafing > 0.0F) {
         var1 -= 90.0F * forward;
      }

      if (mc.thePlayer.moveStrafing < 0.0F) {
         var1 += 90.0F * forward;
      }

      return var1 * ((float) (Math.PI / 180.0));
   }

   public double[] getDirection1(double speed) {
      float dir = this.getDir();
      return new double[]{-(Math.sin((double)dir) * speed), Math.cos((double)dir) * speed};
   }

   public float getDir() {
      float yaw = mc.thePlayer.rotationYaw;
      float f = 1.0F;
      if (mc.thePlayer.moveForward < 0.0F) {
         yaw += 180.0F;
         f = -0.5F;
      } else if (mc.thePlayer.moveForward > 0.0F) {
         f = 0.5F;
      }

      if (mc.thePlayer.moveStrafing > 0.0F) {
         yaw -= 90.0F * f;
      } else if (mc.thePlayer.moveStrafing < 0.0F) {
         yaw += 90.0F * f;
      }

      return (float)Math.toRadians((double)yaw);
   }
}
